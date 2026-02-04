package com.atguigu.examsystem.controller;

import com.atguigu.examsystem.dto.StartExamDto;
import com.atguigu.examsystem.dto.SubmitAnswerDto;
import com.atguigu.examsystem.entity.ExamRecords;
import com.atguigu.examsystem.service.ExamRecordsService;
import com.atguigu.examsystem.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "考试记录管理", description = "考试记录管理")
@RestController
@RequestMapping("/examRecords")
public class ExamRecordsController {

    private final ExamRecordsService examRecordsService;


    public ExamRecordsController(ExamRecordsService examRecordsService) {
        this.examRecordsService = examRecordsService;
    }

    @Operation(summary = "开始考试", description = "开始考试")
    @PostMapping("/startExam")
    public Result<ExamRecords> startExam(@RequestBody StartExamDto dto) {
        ExamRecords exam = examRecordsService.startExam(dto.getExamId(), dto.getStudentName());
        return Result.okData(exam);
    }

    @Operation(summary = "根据id获取考试记录", description = "根据id获取考试记录")
    @GetMapping("/getById/{id}")
    public Result<ExamRecords> getById(@PathVariable Long id) {
        ExamRecords exam = examRecordsService.getExamPaperQues(id);
        return Result.okData(exam);
    }

    @Operation(summary = "提交答案", description = "提交答案")
    @PostMapping("/submitAnswer")
    public Result<String> submitAnswer(@RequestBody SubmitAnswerDto dto) {
        examRecordsService.submitAnswer(dto);
        return Result.okMsg("提交答案成功");
    }
}
