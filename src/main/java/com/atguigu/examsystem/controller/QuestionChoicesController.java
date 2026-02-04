package com.atguigu.examsystem.controller;

import com.atguigu.examsystem.service.QuestionChoicesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "题目选项管理", description = "题目选项管理6")
@RestController
@RequestMapping("/question-choices")
public class QuestionChoicesController {

    private final QuestionChoicesService questionChoicesService;

    public QuestionChoicesController(QuestionChoicesService questionChoicesService) {
        this.questionChoicesService = questionChoicesService;
    }
}
