package com.atguigu.examsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "开始考试Dto")
@Data
public class StartExamDto {

    @Schema(description = "试卷id")
    private Long examId;

    @Schema(description = "学生名称")
    private String studentName;

}
