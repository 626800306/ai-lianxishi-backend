package com.atguigu.examsystem.service;

import com.atguigu.examsystem.dto.SubmitAnswerDto;
import com.atguigu.examsystem.entity.AnswerRecord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AnswerRecordService extends IService<AnswerRecord> {
    void submitAnswer(SubmitAnswerDto dto);
}
