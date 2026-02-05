package com.atguigu.examsystem.kimi;

import lombok.Data;

@Data
public class MarkExamAiRes {

    // 得分
    private Integer score;
    // 评价反馈
    private String feedback;
    // 扣分原因
    private String reason;
}
