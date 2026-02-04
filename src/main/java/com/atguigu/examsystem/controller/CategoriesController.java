package com.atguigu.examsystem.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import com.atguigu.examsystem.entity.Categories;
import com.atguigu.examsystem.service.CategoriesService;
import com.atguigu.examsystem.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "题目分类管理", description = "题目分类管理")
@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }


    @Operation(summary = "获取树结构", description = "获取树结构")
    @GetMapping("/tree")
    public Result<List<Tree<Long>>> tree() {
        List<Tree<Long>> trees = categoriesService.categoriesTree();
        return Result.okData(trees);
    }

    @Operation(summary = "分页查询", description = "分页查询")
    @GetMapping("/page")
    public Result<Page<Categories>> page(@RequestParam("pageNum") Integer pageNum,
                                         @RequestParam("pageSize") Integer pageSize,
                                         @RequestParam(value = "name", required = false) String name) {
        Page<Categories> p = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Categories> wrapper = Wrappers.lambdaQuery(Categories.class).like(StrUtil.isNotBlank(name), Categories::getName, name);
        Page<Categories> page = categoriesService.page(p, wrapper);
        return Result.okData(page);
    }

    @Operation(summary = "新增分类", description = "新增分类")
    @PostMapping("/save")
    public Result<String> save(@RequestBody Categories category) {
        categoriesService.save(category);
        return Result.okMsg("新增成功");
    }

    @PostMapping("/removeById")
    @Operation(summary = "根据ID删除分类", description = "根据ID删除分类")
    public Result<String> removeById(@RequestParam("id") Long id) {
        LambdaQueryWrapper<Categories> wrapper = Wrappers.lambdaQuery(Categories.class).eq(Categories::getParentId, id);
        List<Categories> categoriesList = categoriesService.list(wrapper);
        if (!CollUtil.isEmpty(categoriesList)) {
            return Result.errorMsg("此分类包含子分类，请先删除子分类");
        }
        categoriesService.removeById(id);
        return Result.okMsg("删除成功");
    }

    @PostMapping("/updateById")
    @Operation(summary = "根据ID修改分类", description = "根据ID修改分类")
    public Result<String> updateById(@RequestBody Categories category) {
        categoriesService.updateById(category);
        return Result.okMsg("更新成功");
    }
}
