package com.atguigu.examsystem.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.atguigu.examsystem.entity.Categories;
import com.atguigu.examsystem.mapper.CategoriesMapper;
import com.atguigu.examsystem.service.CategoriesService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CategoriesServiceImpl extends ServiceImpl<CategoriesMapper, Categories>
        implements CategoriesService {

    private final CategoriesMapper categoriesMapper;

    public CategoriesServiceImpl(CategoriesMapper categoriesMapper) {
        this.categoriesMapper = categoriesMapper;
    }

    @Override
    public List<Tree<Long>> categoriesTree() {
        QueryWrapper<Categories> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        List<Categories> categories = categoriesMapper.selectList(wrapper);
        // 构造TreeNode
        List<TreeNode<Long>> treeNodes = categories.stream().map(c -> {
            TreeNode<Long> treeNode = new TreeNode<>();
            treeNode.setId(c.getId());
            treeNode.setName(c.getName());
            treeNode.setParentId(c.getParentId());
            treeNode.setWeight(c.getSort());
            treeNode.setExtra(Map.of("createTime", c.getCreateTime(),
                    "updateTime", c.getUpdateTime(),
                    "isDeleted", c.getIsDeleted()));
            return treeNode;
        }).toList();
        // 构造Tree
        List<Tree<Long>> trees = TreeUtil.build(treeNodes, 0L);
        return trees;
    }
}
