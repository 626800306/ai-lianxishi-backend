package com.atguigu.examsystem.service;

import cn.hutool.core.lang.tree.Tree;
import com.atguigu.examsystem.entity.Categories;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoriesService extends IService<Categories> {

    List<Tree<Long>> categoriesTree();
}
