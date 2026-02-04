package com.atguigu.examsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图表
 */
@Schema(description = "轮播图")
@TableName("banners")
@Data
public class Banners {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "标题")
    @TableField(value = "title")
    private String title;

    @Schema(description = "描述")
    @TableField(value = "description")
    private String description;

    @Schema(description = "图片地址")
    @TableField(value = "image_url")
    private String imageUrl;

    @Schema(description = "点击跳转地址")
    @TableField(value = "link_url")
    private String linkUrl;

    @Schema(description = "排序顺序，数字越小越靠前")
    @TableField(value = "sort_order")
    private Integer sortOrder;

    @Schema(description = "是否启用")
    @TableField(value = "is_active", fill = FieldFill.INSERT)
    private Boolean isActive;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @Schema(description = "是否删除")
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Boolean isDeleted;
}
