package com.atguigu.examsystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "题目分类表")
@TableName("categories")
@Data
public class Categories extends BaseEntity {

    /**
     * 名称
     */
    @Schema(description = "名称")
    @TableField(value = "name")
    private String name;

    /**
     * 父ID
     */
    @Schema(description = "父ID")
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @TableField(value = "sort")
    private Integer sort;


}
