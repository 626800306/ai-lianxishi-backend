package com.atguigu.examsystem.service.impl;

import com.atguigu.examsystem.dto.SubmitAnswerDto;
import com.atguigu.examsystem.entity.AnswerRecord;
import com.atguigu.examsystem.mapper.AnswerRecordMapper;
import com.atguigu.examsystem.service.AnswerRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerRecordServiceImpl extends ServiceImpl<AnswerRecordMapper, AnswerRecord>
        implements AnswerRecordService {

    private final AnswerRecordMapper answerRecordMapper;

    public AnswerRecordServiceImpl(AnswerRecordMapper answerRecordMapper) {
        this.answerRecordMapper = answerRecordMapper;
    }
    @Override
    public void submitAnswer(SubmitAnswerDto dto) {

        List<SubmitAnswerDto.UserAnswers> userAnswers = dto.getUserAnswers();
        List<AnswerRecord> answerRecordList = userAnswers.stream().map(answer -> {
            AnswerRecord answerRecord = new AnswerRecord();
            answerRecord.setExamRecordId(Long.parseLong(dto.getExamRecordId()));
            answerRecord.setQuestionId(Long.parseLong(answer.getQuestionId()));
            answerRecord.setUserAnswer(answer.getUserAnswer());
            return answerRecord;
        }).toList();

        answerRecordMapper.insert(answerRecordList);

    }
}
