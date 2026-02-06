package com.atguigu.examsystem.controller;

import cn.hutool.core.util.StrUtil;
import com.atguigu.examsystem.dto.StartExamDto;
import com.atguigu.examsystem.entity.BaseEntity;
import com.atguigu.examsystem.entity.ExamRecords;
import com.atguigu.examsystem.service.ExamRecordsService;
import com.atguigu.examsystem.service.PaperService;
import com.atguigu.examsystem.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "考试记录管理", description = "考试记录管理")
@RestController
@RequestMapping("/examRecords")
public class ExamRecordsController {

    private final ExamRecordsService examRecordsService;

    private final PaperService paperService;


    public ExamRecordsController(ExamRecordsService examRecordsService,
                                 PaperService paperService) {
        this.examRecordsService = examRecordsService;
        this.paperService = paperService;
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

    @Operation(summary = "根据试卷id获取考试记录", description = "根据试卷id获取考试记录")
    @GetMapping("/list")
    public Result<List<ExamRecords>> list(@RequestParam(value = "paperId", required = false) String paperId,
                                          @RequestParam("limit") Integer limit) {

        LambdaQueryWrapper<ExamRecords> wrapper = Wrappers.lambdaQuery(ExamRecords.class)
                .eq(StrUtil.isNotEmpty(paperId), ExamRecords::getExamId, paperId)
                .eq(ExamRecords::getStatus, "已批阅").orderByDesc(ExamRecords::getScore, BaseEntity::getCreateTime);
        wrapper.last("limit " + limit);
        List<ExamRecords> examRecords = examRecordsService.list(wrapper);
        examRecords.stream().forEach(exam ->
                exam.setPaper(paperService.getById(exam.getExamId())));

        return Result.okData(examRecords);
    }

}
