package com.atguigu.examsystem.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.atguigu.examsystem.dto.SubmitAnswerDto;
import com.atguigu.examsystem.entity.*;
import com.atguigu.examsystem.enums.ExamRecordsEnum;
import com.atguigu.examsystem.enums.QuestionTypeEnum;
import com.atguigu.examsystem.kimi.KimiAiService;
import com.atguigu.examsystem.kimi.MarkExamAiRes;
import com.atguigu.examsystem.mapper.AnswerRecordMapper;
import com.atguigu.examsystem.mapper.ExamRecordsMapper;
import com.atguigu.examsystem.service.AnswerRecordService;
import com.atguigu.examsystem.service.ExamRecordsService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnswerRecordServiceImpl extends ServiceImpl<AnswerRecordMapper, AnswerRecord>
        implements AnswerRecordService {

    private final AnswerRecordMapper answerRecordMapper;

    private final ExamRecordsMapper examRecordsMapper;

    private final ExamRecordsService examRecordsService;

    private final KimiAiService kimiAiService;

    public AnswerRecordServiceImpl(AnswerRecordMapper answerRecordMapper,
                                   ExamRecordsMapper examRecordsMapper,
                                   ExamRecordsService examRecordsService,
                                   KimiAiService kimiAiService) {
        this.answerRecordMapper = answerRecordMapper;
        this.examRecordsMapper = examRecordsMapper;
        this.examRecordsService =examRecordsService;
        this.kimiAiService = kimiAiService;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAnswer(SubmitAnswerDto dto) {
        // 更新考试记录
        String examRecordId = dto.getExamRecordId();
        ExamRecords examRecords = examRecordsMapper.selectOne(
                Wrappers.lambdaQuery(ExamRecords.class).eq(ExamRecords::getId, examRecordId));
        examRecords.setStatus(ExamRecordsEnum.COMPLETED.getDesc());
        examRecords.setEndTime(LocalDateTime.now());
        examRecordsMapper.updateById(examRecords);

        // 插入考试答案
        List<SubmitAnswerDto.UserAnswers> userAnswers = dto.getUserAnswers();
        List<AnswerRecord> answerRecordList = userAnswers.stream().map(answer -> {
            AnswerRecord answerRecord = new AnswerRecord();
            answerRecord.setExamRecordId(Long.parseLong(examRecordId));
            answerRecord.setQuestionId(Long.parseLong(answer.getQuestionId()));
            answerRecord.setUserAnswer(answer.getUserAnswer());
            return answerRecord;
        }).toList();
        answerRecordMapper.insert(answerRecordList);

        // 判卷
        // 选择题/判断题  简答题

        // 获取试卷题目
        ExamRecords exam = examRecordsService.getExamPaperQues(Long.parseLong(examRecordId));
        Paper paper = exam.getPaper();
        List<Questions> questions = paper.getQuestions();
        // 将试卷试题questions转换为Map<Long, Questions>(id,Questions)
        Map<Long, Questions> questionsMap = questions.stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        Integer totalScore = 0; // 试卷总得分
        // 遍历用户答案，跟试题正确答案相对比
        for (AnswerRecord answerRecord : answerRecordList) {
            Long questionId = answerRecord.getQuestionId();
            String userAnswer = answerRecord.getUserAnswer();
            // 根据试题id questionId获取试题
            Questions quest = questionsMap.get(questionId);
            // 判断题/简答题正确答案

            // 判断题
            if (QuestionTypeEnum.JUDGE.getType().equalsIgnoreCase(quest.getType())) {
                Questions.Answer ans = quest.getAnswer();
                String rightAnswer = ans.getAnswer();
                if (userAnswer.equalsIgnoreCase(rightAnswer)) {
                    answerRecord.setIsCorrect(Boolean.TRUE);
                    answerRecord.setScore(quest.getScore());
                }
            }
            // 选择题
            if (QuestionTypeEnum.CHOICE.getType().equalsIgnoreCase(quest.getType())) {
                Questions.Answer ans = quest.getAnswer();
                String rightAnswer = ans.getAnswer();
                if (userAnswer.equalsIgnoreCase(rightAnswer)) {
                    answerRecord.setScore(quest.getScore());
                }
            }
            // 简答题
            if (QuestionTypeEnum.TEXT.getType().equalsIgnoreCase(quest.getType())) {
                // 调用Kimi AI判断
                String prompt = buildPrompt(quest, userAnswer);
                String res = kimiAiService.callKimiAi(prompt);
                JSONObject jsonObject = JSONUtil.parseObj(res);
                JSONArray array = (JSONArray) jsonObject.get("choices");
                JSONObject obj = (JSONObject) array.get(0);
                JSONObject jo = (JSONObject) obj.get("message");
                String content = jo.get("content").toString();
                MarkExamAiRes markExamAiRes = JSONUtil.toBean(content, MarkExamAiRes.class);
                answerRecord.setScore(markExamAiRes.getScore());
                answerRecord.setAiCorrection(markExamAiRes.getReason());
            }
            // 试卷总得分计算
            totalScore += answerRecord.getScore();
        }
        
        // 更新试卷答案记录
        updateBatchById(answerRecordList);

        // 更新考试记录
        examRecords.setStatus(ExamRecordsEnum.APPROVED.getDesc());
        examRecords.setScore(totalScore);
        examRecords.setAnswers("本次考试成绩还不错！望再接再厉！加油！");
        examRecordsService.updateById(examRecords);


    }


    private String buildPrompt(Questions questions,
                               String userAnswer) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一名专业的考试阅卷老师，请对以下题目进行判卷：\n");

        prompt.append("【题目信息】\n");
        prompt.append("题型：").append(QuestionTypeEnum.getDescByType(questions.getType())).append("\n");
        prompt.append("题目：").append(questions.getTitle()).append("\n");
        prompt.append("标准答案：").append(questions.getAnswer()).append("\n");
        prompt.append("满分：").append(questions.getScore()).append("\n");

        prompt.append("【学生答案】\n");
        prompt.append(userAnswer).append("\n");

        prompt.append("【判卷要求】\n");
        prompt.append("- 主观题：根据答案的准确性、完整性、逻辑性进行评分\n");
        prompt.append("- 答案要点正确且完整：80-100%分数\n");
        prompt.append("- 答案基本正确但不够完整：60-80%分数\n");
        prompt.append("- 答案部分正确：30-60分数\n");
        prompt.append("- 答案完全错误或未作答：0分\n");

        prompt.append("请按以下JSON格式返回判卷结果：\n");
        prompt.append("{\n");
        prompt.append("    score: 实际得分（整数）");
        prompt.append("    feedback: 具体的评价反馈（50字以内）");
        prompt.append("    reason: 扣分原因或得分依据（30字以内）");
        prompt.append("}\n");

        return prompt.toString();
    }
}
