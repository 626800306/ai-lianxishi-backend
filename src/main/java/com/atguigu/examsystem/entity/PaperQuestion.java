package com.atguigu.examsystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "试卷题目表")
public class PaperQuestion extends BaseEntity {

    @Schema(description = "试卷id")
    @TableField("paper_id")
    private Long paperId;

    @Schema(description = "题目id")
    @TableField("question_id")
    private Long questionId;

    @Schema(description = "题目分值")
    @TableField("score")
    private Integer score;
}
