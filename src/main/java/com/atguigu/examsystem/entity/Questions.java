package com.atguigu.examsystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "题目表")
@Data
@TableName("questions")
public class Questions extends BaseEntity {

    @Schema(description = "题目内容")
    @TableField(value = "title")
    private String title;

    @Schema(description = "题目类型")
    @TableField(value = "type")
    private String type;

    @Schema(description = "题目选项")
    @TableField(value = "multi")
    private Boolean multi;

    @Schema(description = "题目分类ID")
    @TableField(value = "category_id")
    private Long categoryId;

    @Schema(description = "题目难度")
    @TableField(value = "difficulty")
    private String difficulty;

    @Schema(description = "题目得分")
    @TableField(value = "score")
    private Integer score;

    @Schema(description = "题目分析")
    @TableField(value = "analysis")
    private String analysis;


    @Schema(description = "题目答案 判断题/简答题")
    @TableField(exist = false)
    private Answer answer;

    @Schema(description = "题目选项列表")
    @TableField(exist = false)
    private List<Choices> choices;




    @Schema(description = "题目选项")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Choices {

        @Schema(description = "选项内容")
        private String content;

        @Schema(description = "是否正确选项")
        private Boolean isCorrect;
    }

    @Schema(description = "题目答案")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Answer {


        @Schema(description = "答案内容")
        private String answer;
    }
}
