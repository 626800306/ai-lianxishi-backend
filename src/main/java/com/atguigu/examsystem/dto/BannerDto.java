package com.atguigu.examsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "轮播图dto")
public class BannerDto {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图片url")
    private String imageUrl;

    @Schema(description = "链接地址")
    private String linkUrl;

    @Schema(description = "排序")
    private Integer sortOrder;
}
