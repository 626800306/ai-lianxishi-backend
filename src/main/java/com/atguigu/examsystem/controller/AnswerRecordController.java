package com.atguigu.examsystem.controller;

import com.atguigu.examsystem.dto.SubmitAnswerDto;
import com.atguigu.examsystem.service.AnswerRecordService;
import com.atguigu.examsystem.vo.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "答案记录表", description = "答案记录表")
@RestController
@RequestMapping("/answerRecord")
public class AnswerRecordController {

    private final AnswerRecordService answerRecordService;

    public AnswerRecordController(AnswerRecordService answerRecordService) {
        this.answerRecordService = answerRecordService;
    }

    @PostMapping("/sumbitAnswer")
    public Result<String> submitAnswer(@RequestBody SubmitAnswerDto dto) {
        answerRecordService.submitAnswer(dto);
        return Result.okMsg("提交答案成功");
    }
}
