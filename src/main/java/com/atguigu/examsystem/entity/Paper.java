package com.atguigu.examsystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "试卷表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("paper")
public class Paper extends BaseEntity {

    @Schema(description = "试卷名称")
    @TableField("name")
    private String name;

    @Schema(description = "试卷描述")
    @TableField("description")
    private String description;

    @Schema(description = "试卷状态")
    @TableField("status")
    private String status;

    @Schema(description = "试卷总分")
    @TableField("total_score")
    private BigDecimal totalScore;

    @Schema(description = "试卷总题数")
    @TableField("question_count")
    private Integer questionCount;

    @Schema(description = "试卷总时长")
    @TableField("duration")
    private Integer duration;

    @Schema(description = "试卷试题")
    @TableField(exist = false)
    private List<Questions> questions;
}

