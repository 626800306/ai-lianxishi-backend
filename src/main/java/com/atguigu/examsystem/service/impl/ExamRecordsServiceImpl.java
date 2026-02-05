package com.atguigu.examsystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.examsystem.entity.*;
import com.atguigu.examsystem.enums.ExamRecordsEnum;
import com.atguigu.examsystem.enums.QuestionTypeEnum;
import com.atguigu.examsystem.mapper.*;
import com.atguigu.examsystem.service.ExamRecordsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExamRecordsServiceImpl extends ServiceImpl<ExamRecordsMapper, ExamRecords>
        implements ExamRecordsService {

    private final ExamRecordsMapper examRecordsMapper;

    private final PaperMapper paperMapper;

    private final PaperQuestionMapper paperQuestionMapper;

    private final QuestionsMapper questionsMapper;

    private final QuestionChoicesMapper questionChoicesMapper;

    private final QuestionAnswersMapper questionAnswersMapper;

    AtomicInteger atomicIn = new AtomicInteger();

    public ExamRecordsServiceImpl(ExamRecordsMapper examRecordsMapper,
                                  PaperMapper paperMapper,
                                  PaperQuestionMapper paperQuestionMapper,
                                  QuestionsMapper questionsMapper,
                                  QuestionChoicesMapper questionChoicesMapper,
                                  QuestionAnswersMapper questionAnswersMapper) {
        this.examRecordsMapper = examRecordsMapper;
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionsMapper = questionsMapper;
        this.questionChoicesMapper = questionChoicesMapper;
        this.questionAnswersMapper = questionAnswersMapper;
    }

    @Override
    public ExamRecords startExam(Long examId, String studentName) {
        LambdaQueryWrapper<ExamRecords> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecords::getExamId, examId);
        wrapper.eq(ExamRecords::getStudentName, studentName);
        ExamRecords examRecords = examRecordsMapper.selectOne(wrapper);
        if (examRecords != null) {
            // 设置切屏次数
            examRecords.setWindowSwitches(atomicIn.getAndIncrement());
            // 跟新考试记录
            examRecordsMapper.update(examRecords,
                    new UpdateWrapper<ExamRecords>()
                            .eq("exam_id", examId)
                            .eq("student_name", studentName));
        } else {
            // 插入一条考试记录 examRecords
            examRecords = new ExamRecords();
            examRecords.setExamId(examId);
            examRecords.setStudentName(studentName);
            examRecords.setStartTime(LocalDateTime.now());
            // 初始化状态 进行中
            examRecords.setStatus(ExamRecordsEnum.UNDERWAY.getDesc());
            // 切屏次数
            examRecords.setWindowSwitches(0);

            examRecordsMapper.insert(examRecords);
        }
        return examRecords;
    }

    @Override
    public ExamRecords getExamPaperQues(Long id) {
        ExamRecords exam = examRecordsMapper.selectById(id);
        // 根据试卷id获取试卷
        Paper paper = paperMapper.selectOne(
                new LambdaQueryWrapper<Paper>().eq(BaseEntity::getId, exam.getExamId()));
        exam.setDuration(paper.getDuration());

        // 根据试卷id获取题目
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, exam.getExamId()).orderByDesc(BaseEntity::getCreateTime));
        List<Long> questionIdList = paperQuestions.stream().map(PaperQuestion::getQuestionId).toList();
        List<Questions> questions = questionsMapper.selectByIds(questionIdList);

        // 设置题目选择题：选项；
        // 判断题/简答题：答案；
        questions.stream().forEach(q -> {
            String type = q.getType();
            Long questionId = q.getId();
            // 选择题
            if (QuestionTypeEnum.CHOICE.getType().equals(type)) {
                List<QuestionChoices> questionChoices = questionChoicesMapper.selectList(
                        Wrappers.lambdaQuery(QuestionChoices.class).eq(QuestionChoices::getQuestionId, questionId).orderByDesc(BaseEntity::getCreateTime));
                List<Questions.Choices> choices = BeanUtil.copyToList(questionChoices, Questions.Choices.class);
                // 选择题 设置选项
                q.setChoices(choices);
                String an = "";
                // 设置答案
                for (int i = 0; i < questionChoices.size(); i++) {
                    QuestionChoices qc = questionChoices.get(i);
                    if (qc.getIsCorrect()) {
                        an += (numberToLetter(i) + ",");
                    }
                }
                // 设置判断题答案 格式A,B,C
                q.setAnswer(new Questions.Answer(an.substring(0, an.length() - 1)));

            }
            // 判断题/简答题
            if (QuestionTypeEnum.JUDGE.getType().equals(type) ||
                    QuestionTypeEnum.TEXT.getType().equals(type)) {
                QuestionAnswers questionAnswers = questionAnswersMapper.selectOne(
                        Wrappers.lambdaQuery(QuestionAnswers.class).eq(QuestionAnswers::getQuestionId, questionId));
                Questions.Answer answer = BeanUtil.copyProperties(questionAnswers, Questions.Answer.class);
                // 判断题/简答题 设置答案
                q.setAnswer(answer);
            }
        });

        paper.setQuestions(questions);

        exam.setPaper(paper);

        return exam;
    }


    public static char numberToLetter(int number) {
        if (number < 0 || number > 25) {
            throw new IllegalArgumentException("数字必须在0-25范围内");
        }
        return (char) ('A' + number);
    }


}
