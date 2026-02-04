package com.atguigu.examsystem.service;

import com.atguigu.examsystem.dto.SubmitAnswerDto;
import com.atguigu.examsystem.entity.ExamRecords;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ExamRecordsService extends IService<ExamRecords> {

    ExamRecords startExam(Long examId, String studentName);

    ExamRecords getExamPaperQues(Long id);

    void submitAnswer(SubmitAnswerDto dto);
}
