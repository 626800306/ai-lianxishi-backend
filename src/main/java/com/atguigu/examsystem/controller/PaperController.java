package com.atguigu.examsystem.controller;

import cn.hutool.core.util.StrUtil;
import com.atguigu.examsystem.dto.AiCreatePaperDto;
import com.atguigu.examsystem.dto.ManualCreatePaperDto;
import com.atguigu.examsystem.entity.BaseEntity;
import com.atguigu.examsystem.entity.Paper;
import com.atguigu.examsystem.entity.PaperQuestion;
import com.atguigu.examsystem.service.PaperQuestionService;
import com.atguigu.examsystem.service.PaperService;
import com.atguigu.examsystem.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "试卷管理模块", description = "试卷管理模块")
@RestController
@RequestMapping("/paper")
public class PaperController {

    private final PaperService paperService;

    private final PaperQuestionService paperQuestionService;

    public PaperController(PaperService paperService,
                           PaperQuestionService paperQuestionService) {
        this.paperService = paperService;
        this.paperQuestionService = paperQuestionService;
    }

    @Operation(summary = "分页获取试卷", description = "分页获取试卷")
    @GetMapping("/page")
    public Result<List<Paper>> pageList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "1000") Integer pageSize,
                                        @RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "status", required = false) String status) {
        Page<Paper> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(name), Paper::getName, name);
        wrapper.eq(StrUtil.isNotEmpty(status), Paper::getStatus, status);
        wrapper.orderByDesc(BaseEntity::getCreateTime);
        List<Paper> p = paperService.list(page, wrapper);
        return Result.okData(p);
    }

    @Operation(summary = "根据id删除试卷", description = "根据id删除试卷")
    @PostMapping("/removeById")
    public Result<String> removeById(@RequestParam("id") Long id) {
        paperService.removePaperById(id);
        return Result.okMsg("删除成功");
    }

    @Operation(summary = "批量删除试卷", description = "批量删除试卷")
    @PostMapping("/batchRemoveByIds")
    public Result<String> batchRemoveByIds(@RequestBody List<Long> ids) {
        // 删除试卷
        paperService.removeByIds(ids);
        // 删除试卷试题
        paperQuestionService.remove(Wrappers.lambdaQuery(PaperQuestion.class).in(PaperQuestion::getPaperId, ids));
        return Result.okMsg("批量删除成功");
    }

    @Operation(summary = "根据id获取试卷", description = "根据id获取试卷")
    @GetMapping("/getById")
    public Result<ManualCreatePaperDto> getById(@RequestParam("id") Long id) {
        ManualCreatePaperDto dto = paperService.getPaperById(id);
        return Result.okData(dto);
    }

    @Operation(summary = "更新发布状态", description = "跟新发布状态")
    @PostMapping("/updateStatus")
    public Result<String> updateStatus(@RequestParam("id") Long id,
                                       @RequestParam("status") String status) {
        LambdaUpdateWrapper<Paper> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Paper::getStatus, status);
        wrapper.eq(BaseEntity::getId, id);
        paperService.update(wrapper);
        return Result.okMsg("更新状态成功");
    }


    @Operation(summary = "创建试卷", description = "创建试卷")
    @PostMapping("/createPaper")
    public Result<String> createPaper(@RequestBody ManualCreatePaperDto dto) {
        paperService.savePaper(dto);
        return Result.okMsg("创建成功");
    }

    @Operation(summary = "ai智能组卷", description = "ai智能组卷")
    @PostMapping("/aiCreatePaper")
    public Result<String> aiCreatePaper(@RequestBody AiCreatePaperDto dto) {
        paperService.aiCreatePaper(dto);
        return Result.okMsg("ai智能组卷成功");
    }

    @Operation(summary = "根据id获取试卷信息", description = "根据id获取试卷信息")
    @GetMapping("/getPaperById")
    public Result<Paper> getPaperById(@RequestParam("id") Long id) {
        return Result.okData(paperService.getById(id));
    }
}
