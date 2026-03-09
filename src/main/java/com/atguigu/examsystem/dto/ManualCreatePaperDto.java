package com.atguigu.examsystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ManualCreatePaperDto {

    // 试卷名称
    private String name;

    // 考试持续时间
    private Integer duration;

    // 试卷描述
    private String description;
    // 试题
    private Map<String, Object> questions;

}
