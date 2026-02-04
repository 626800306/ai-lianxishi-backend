package com.atguigu.examsystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "题目选项表")
@Data
@TableName("question_choices")
public class QuestionChoices extends BaseEntity {

    @Schema(description = "题目ID")
    @TableField("question_id")
    private Long questionId;

    @Schema(description = "选项内容")
    @TableField("content")
    private String content;

    @Schema(description = "选项是否正确")
    @TableField("is_correct")
    private Boolean isCorrect;

    @Schema(description = "选项排序")
    @TableField("sort")
    private Integer sort;
}
