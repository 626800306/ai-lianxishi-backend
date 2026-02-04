package com.atguigu.examsystem.controller;

import cn.hutool.json.JSONArray;
import com.atguigu.examsystem.dto.ExcelPreviewDto;
import com.atguigu.examsystem.entity.BaseEntity;
import com.atguigu.examsystem.entity.Questions;
import com.atguigu.examsystem.kimi.AiCreateQuestionsDto;
import com.atguigu.examsystem.kimi.KimiAiService;
import com.atguigu.examsystem.service.QuestionsService;
import com.atguigu.examsystem.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "题目管理", description = "题目管理")
@RestController
@RequestMapping("/questions")
@Slf4j
public class QuestionsController {

    private final QuestionsService questionsService;


    public QuestionsController(QuestionsService questionsService) {

        this.questionsService = questionsService;
    }

    @Operation(summary = "分页查询", description = "分页查询")
    @GetMapping("/page")
    public Result<Page<Questions>> page(@RequestParam("pageNum") Integer pageNum,
                                        @RequestParam("pageSize") Integer pageSize,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "type", required = false) String type,
                                        @RequestParam(value = "difficulty", required = false) String difficulty,
                                        @RequestParam(value = "categoryId", required = false) Long categoryId) {

        /*QueryWrapper<Questions> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(keyword), "title", keyword);
        wrapper.eq(StrUtil.isNotEmpty(type), "type", type);
        wrapper.eq(StrUtil.isNotEmpty(difficulty), "difficulty", difficulty);
        wrapper.orderByDesc("create_time");
        Page<Questions> page = questionsService.page(p, wrapper);*/
        Page<Questions> page = new Page<>(pageNum, pageSize);
        Page p = questionsService.questionsPage(page, keyword, type, difficulty, categoryId);

        return Result.okData(p);
    }





    /*@Operation(summary = "根据ID查询数据", description = "根据ID查询数据")
    @GetMapping("/getById")
    public Result<Questions> getById(@RequestParam("id") Long id) {
        Questions question = questionsService.getById(id);
        return Result.okData(question);
    }*/

    @Operation(summary = "创建题目", description = "创建题目")
    @PostMapping("/save")
    public Result<String> save(@RequestBody Questions question) {
        questionsService.saveQuestion(question);
        return Result.okMsg("创建成功");
    }

    @Operation(summary = "根据ID删除题目", description = "根据ID删除题目")
    @PostMapping("/removeById")
    public Result<String> removeById(@RequestParam("id") Long id) {
        questionsService.removeQuestionById(id);
        return Result.okMsg("删除成功");
    }

    /*@Operation(summary = "根据ID修改题目", description = "根据ID修改题目")
    @PostMapping("/updateById")
    public Result<String> updateById(@RequestBody Questions question) {
        questionsService.updateById(question);
        return Result.okMsg("修改成功");
    }*/

    /*@Operation(summary = "根据ID批量删除题目", description = "根据ID批量删除题目")
    @PostMapping("/removeBatchByIds")
    public Result<String> removeBatchByIds(@RequestBody BatchRemoveQuestionsDto dto) {
        questionsService.batchRemoveQuestion(dto);
        return Result.okMsg("批量删除成功");
    }*/

    @Operation(summary = "根据ID批量删除题目", description = "根据ID批量删除题目")
    @PostMapping("/removeBatchByIds")
    public Result<String> removeBatchByIds(@RequestBody List<Long> ids) {
        questionsService.batchRemoveQuestionByIds(ids);
        return Result.okMsg("批量删除成功");
    }

    @Operation(summary = "下面题目导入模板", description = "下载题目导入模板")
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        questionsService.downloadTemplate(response);
    }


    @Operation(summary = "预览Excel文件", description = "预览Excel文件")
    @PostMapping("/previewExcel")
    public Result<List<ExcelPreviewDto>> previewExcel(@RequestParam("file") MultipartFile file) {
        List<ExcelPreviewDto> excelPrivicwList = questionsService.previewExcel(file);
        return Result.okData(excelPrivicwList);
    }


    @Operation(summary = "导入预览题目", description = "导入预览题目")
    @PostMapping("/importQuestions")
    public Result<String> importQuestionsTemplate(@RequestBody List<ExcelPreviewDto> excelPreviewDtoList) {
        return questionsService.importQuestions(excelPreviewDtoList);
    }

    @Operation(summary = "调用Kimi AI接口", description = "调用Kimi AI接口")
    @PostMapping("/kimiAiCreateQuestions")
    public Result<JSONArray> callKimiAi(@RequestBody AiCreateQuestionsDto dto) {
        JSONArray array = questionsService.callKimiAi(dto);
        return Result.okData(array);
    }
}
