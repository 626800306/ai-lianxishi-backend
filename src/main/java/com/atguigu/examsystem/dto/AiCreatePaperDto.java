package com.atguigu.examsystem.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AiCreatePaperDto {

    // 试卷名称
    private String name;

    // 考试持续时长
    private Integer duration;

    // 试卷描述
    private String description;

    // 选择题/判断题/简答题规则
    private List<Map<String, Object>> rules;
}
