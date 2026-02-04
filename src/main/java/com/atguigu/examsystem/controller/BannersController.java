package com.atguigu.examsystem.controller;

import cn.hutool.core.util.StrUtil;
import com.atguigu.examsystem.entity.Banners;
import com.atguigu.examsystem.service.BannersService;
import com.atguigu.examsystem.vo.BannersVo;
import com.atguigu.examsystem.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/banners")
@Tag(name = "轮播图管理", description = "轮播图相关操作，包括图片上传")
public class BannersController {

    private final BannersService bannersService;

    public BannersController(BannersService bannersService) {
        this.bannersService = bannersService;
    }


    @Operation(summary = "查询所有轮播图（删除不删除都查）", description = "删除所有轮播图（删除不删除都查）")
    @GetMapping("/selectAll")
    public Result<Page<Banners>> selectAll(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                           @RequestParam(value = "title", required = false) String title,
                                           @RequestParam(value = "description", required = false) String description) {
        Page<Banners> page = new Page<>(pageNum, pageSize);
        Page<Banners> p = bannersService.selectAll(page, title, description);
        return Result.okData(p);
    }

    @GetMapping("/select-all")
    @Operation(summary = "查询所有轮播图", description = "查询数据库中所有的轮播图")
    public Result<List<Banners>> selectAll(BannersVo bannersVo) {
        // 以排序顺序展示
        QueryWrapper<Banners> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(bannersVo.getTitle()), "title", bannersVo.getTitle());
        wrapper.like(StringUtils.isNotBlank(bannersVo.getDescription()), "description", bannersVo.getDescription());
        wrapper.orderByDesc("create_time");
        List<Banners> list = bannersService.list(wrapper);
        return Result.okData(list);
    }

    @Operation(summary = "查询已开启的轮播图", description = "查询已开启的轮播图")
    @GetMapping("/active")
    public Result<List<Banners>> selectActiveBanners() {
        QueryWrapper<Banners> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Banners::getIsActive, 1)
                .orderByDesc(Banners::getCreateTime);
        List<Banners> bannersList = bannersService.list(wrapper);
        return Result.okData(bannersList);
    }

    @GetMapping("/page-select-all")
    @Operation(summary = "分页查询轮播图", description = "分页查询轮播图")
    public Result<Page<Banners>> pageSelectAll(
            @Parameter(description = "页码")
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页显示多少条")
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "标题")
            @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "描述")
            @RequestParam(value = "description", required = false) String description) {

        Page<Banners> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Banners> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(title), Banners::getTitle, title);
        wrapper.like(StrUtil.isNotBlank(description), Banners::getDescription, description);
        wrapper.orderByDesc(Banners::getCreateTime);
        Page<Banners> p = bannersService.page(page, wrapper);
        return Result.okData(p);
    }


    @PostMapping("/save")
    @Operation(summary = "上传轮播图", description = "上传轮播图信息")
    public Result<Void> insertBanner(
            @Parameter(description = "轮播图dto")
            @RequestBody Banners banner) {
        if (banner == null) {
            return Result.errorMsg("参数不能为空");
        }
        bannersService.insertBanner(banner);
        return Result.okMsg("添加成功");
    }


    @PostMapping("update")
    @Operation(summary = "更新轮播图", description = "更新轮播图")
    public Result<String> update(@RequestBody Banners banner) {
        UpdateWrapper<Banners> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(Banners::getId, banner.getId());
        bannersService.update(banner, wrapper);
        return Result.okMsg("修改成功");
    }

    @Operation(summary = "删除轮播图", description = "删除轮播图")
    @PostMapping("/removeById")
    public Result<String> removeById(@RequestParam("id") Long id) {
        bannersService.removeById(id);
        return Result.okMsg("删除成功");
    }

    @Operation(summary = "根据ID查询数据", description = "根据ID查询数据")
    @GetMapping("/getById")
    public Result<Banners> getById(Long id) {
        if (id == null) {
            return Result.errorMsg("请输入id值");
        }
        Banners banner = bannersService.getById(id);
        return Result.okData(banner);
    }


    @Operation(summary = "切换轮播图开关", description = "切换轮播图开关")
    @PostMapping("/switch-status")
    public Result<String> updateById(@RequestParam("id") Long id) {
        Banners banner = bannersService.getById(id);
        if (banner.getIsActive()) {
            banner.setIsActive(false);
        } else {
            banner.setIsActive(true);
        }
        bannersService.updateById(banner);
        return Result.okMsg("切换成功");
    }

}
