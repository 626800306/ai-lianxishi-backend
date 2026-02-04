package com.atguigu.examsystem.controller;

import com.atguigu.examsystem.service.QuestionAnswersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "题目选项管理", description = "题目选项管理")
@RestController
@RequestMapping("/question-choices")
public class QuestionAnswersController {

    private final QuestionAnswersService questionAnswersService;

    public QuestionAnswersController(QuestionAnswersService questionChoicesService) {
        this.questionAnswersService = questionChoicesService;
    }


}
