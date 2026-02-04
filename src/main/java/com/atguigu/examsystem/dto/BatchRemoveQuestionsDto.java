package com.atguigu.examsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchRemoveQuestionsDto {

    private List<Long> ids;
}
