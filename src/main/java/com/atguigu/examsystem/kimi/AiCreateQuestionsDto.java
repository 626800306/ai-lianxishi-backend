package com.atguigu.examsystem.kimi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiCreateQuestionsDto {

    // 题目分类id
    private String categoryId;

    // 题目数量
    private Integer count;

    // 题目难度
    private String difficulty;

    // 是否包含多选
    private Boolean includeMultiple;

    // 额外要求
    private String requirements;

    // 主题
    private String topic;

    // 题目类型
    private String types;
}
