package com.atguigu.examsystem.dto;

import com.atguigu.examsystem.entity.Questions;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "预览ExcelDto")
@Data
public class ExcelPreviewDto {


    // 题目内容
    @Schema(description = "题目内容")
    private String title;

    // 题目类型
    @Schema(description = "题目类型CHOICE/JUDGE/TEXT")
    private String type;

    // 题目分类id
    @Schema(description = "题目分类id")
    private String categoryId;

    // 是否多选
    @Schema(description = "是否多选 0:false 1:true")
    private Boolean multi;


    // 题目难度
    @Schema(description = "题目难度EASY/MEDIUM/HARD")
    private String difficulty;

    // 题目分值
    @Schema(description = "题目分值")
    private String score;

    // 选择题选项列表
    @Schema(description = "选择题选项")
    private List<Questions.Choices> choices;
    // 简答题答案
    @Schema(description = "简答题答案")
    private Questions.Answer answer;

    // 题目解析
    @Schema(description = "题目解析")
    private String analysis;

}
