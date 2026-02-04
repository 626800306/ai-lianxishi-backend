package com.atguigu.examsystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.atguigu.examsystem.dto.AiCreatePaperDto;
import com.atguigu.examsystem.dto.ManualCreatePaperDto;
import com.atguigu.examsystem.entity.BaseEntity;
import com.atguigu.examsystem.entity.Paper;
import com.atguigu.examsystem.entity.PaperQuestion;
import com.atguigu.examsystem.entity.Questions;
import com.atguigu.examsystem.enums.PaperStatusEnum;
import com.atguigu.examsystem.mapper.PaperMapper;
import com.atguigu.examsystem.mapper.PaperQuestionMapper;
import com.atguigu.examsystem.mapper.QuestionsMapper;
import com.atguigu.examsystem.service.PaperService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    private final PaperMapper paperMapper;

    private final PaperQuestionMapper paperQuestionMapper;

    private final QuestionsMapper questionsMapper;

    public PaperServiceImpl(PaperMapper paperMapper,
                            PaperQuestionMapper paperQuestionMapper,
                            QuestionsMapper questionsMapper) {
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionsMapper = questionsMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePaper(ManualCreatePaperDto dto) {

        Paper paper = new Paper();
        paper.setName(dto.getName());
        paper.setDescription(dto.getDescription());
        paper.setStatus(PaperStatusEnum.DRAFT.getStatus());
        paper.setDuration(dto.getDuration());

        Map<String, Object> questions = dto.getQuestions();
        // 总题数
        paper.setQuestionCount(questions.size());
        // 总分值
        int totalScore = questions.values().stream().map(Object::toString).mapToInt(Integer::parseInt).sum();
        paper.setTotalScore(new BigDecimal(totalScore));

        // 保存试卷
        paperMapper.insert(paper);

        List<PaperQuestion> paperQuestionList = questions.entrySet().stream().map(q -> {
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paper.getId());
            paperQuestion.setQuestionId(Long.parseLong(q.getKey()));
            paperQuestion.setScore(Integer.parseInt(q.getValue().toString()));
            return paperQuestion;
        }).toList();

        // 批量保存试卷试题
        paperQuestionMapper.insert(paperQuestionList);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePaperById(Long id) {
        // 删除试卷
        paperMapper.deleteById(id);
        // 删除试卷题目
        LambdaQueryWrapper<PaperQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperQuestion::getPaperId, id);
        paperQuestionMapper.delete(wrapper);
    }

    @Override
    public ManualCreatePaperDto getPaperById(Long id) {
        ManualCreatePaperDto dto = new ManualCreatePaperDto();

        Paper paper = paperMapper.selectById(id);
        dto.setName(paper.getName());
        dto.setDescription(paper.getDescription());
        dto.setDuration(paper.getDuration());


        Map<String, Object> questions = new HashMap<>();
        LambdaQueryWrapper<PaperQuestion> wrapper = Wrappers.lambdaQuery(PaperQuestion.class);
        wrapper.eq(PaperQuestion::getPaperId, id);
        List<Map<String, Object>> paperQuestionList = paperQuestionMapper.selectMaps(wrapper);
        for (Map<String, Object> map : paperQuestionList) {
            questions.put(map.get("question_id").toString(), map.get("score"));
        }
        dto.setQuestions(questions);

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void aiCreatePaper(AiCreatePaperDto dto) {
        Paper paper = new Paper();

        BeanUtil.copyProperties(dto, paper);
        // 初始化试卷草稿状态
        paper.setStatus(PaperStatusEnum.DRAFT.getStatus());

        List<Map<String, Object>> rules = dto.getRules();

        // 计算总分数
        int totalScore = rules.stream().mapToInt(m -> {
            int count = Integer.parseInt(m.get("count").toString());
            int score = Integer.parseInt(m.get("score").toString());
            return count * score;
        }).sum();
        paper.setTotalScore(new BigDecimal(totalScore));

        // 计算总题数
        int totalQuestion = rules.stream()
                .mapToInt(m -> Integer.parseInt(m.get("count").toString()))
                .sum();
        paper.setQuestionCount(totalQuestion);

        // 插入试题
        paperMapper.insert(paper);

        rules.stream().forEach(map -> {
            // 试题类型
            String type = map.get("type").toString();
            List<String> categoryList = (ArrayList)map.get("categoryIds");
            int count = Integer.parseInt(map.get("count").toString());
            int score = Integer.parseInt(map.get("score").toString());
            // 查询试题id集合
            List<Long> questionIdList;
            if (!CollUtil.isEmpty(categoryList)) {
                questionIdList = questionsMapper.selectList(
                                Wrappers.lambdaQuery(Questions.class).in(Questions::getCategoryId, categoryList))
                        .stream().map(BaseEntity::getId).toList();
            } else {
                questionIdList = questionsMapper.selectList(null).stream().map(BaseEntity::getId).toList();
            }
                // 试题id集合
                Set<Long> ids = RandomUtil.randomEleSet(questionIdList, count);

                // 组装paperQuestion
                List<PaperQuestion> paperQuestionList = ids.stream().map(id -> {
                    PaperQuestion paperQuestion = new PaperQuestion();
                    paperQuestion.setPaperId(paper.getId());
                    paperQuestion.setQuestionId(id);
                    paperQuestion.setScore(score);
                    return paperQuestion;
                }).toList();

                paperQuestionMapper.insert(paperQuestionList);

        });
    }
}
