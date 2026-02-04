package com.atguigu.examsystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "考试答案记录表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("answer_record")
public class AnswerRecord extends BaseEntity {

    @Schema(description = "考试记录id")
    @TableField("exam_record_id")
    private Long examRecordId;

    @Schema(description = "试题id")
    @TableField("question_id")
    private Long questionId;

    @Schema(description = "用户答案")
    @TableField("user_answer")
    private String userAnswer;

    @Schema(description = "试题得分")
    @TableField("score")
    private Integer score;

    @Schema(description = "答案是否正确")
    @TableField("is_correct")
    private Boolean isCorrect;

    @Schema(description = "ai判断")
    @TableField("ai_correction")
    private String aiCorrection;
}
