package com.atguigu.examsystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "题目答案表")
@TableName("question_answers")
@Data
public class QuestionAnswers extends BaseEntity {

    @Schema(description = "题目ID")
    @TableField("question_id")
    private Long questionId;

    @Schema(description = "答案")
    @TableField("answer")
    private String answer;

    @Schema(description = "关键词")
    @TableField("keywords")
    private String keywords;

}
