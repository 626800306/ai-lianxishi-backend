package com.atguigu.examsystem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 轮播图搜索
 */
@Data
@Schema(description = "轮播图Vo")
public class BannersVo {
    @Schema(description = "名称")
    // 名称
    private String title;
    @Schema(description = "描述")
    // 描述
    private String description;
}
