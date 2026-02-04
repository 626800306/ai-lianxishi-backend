package com.atguigu.examsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "提交答案dto")
@Data
public class SubmitAnswerDto {

    @Schema(description = "考试记录id")
    private String examRecordId;

    @Schema(description = "用户答案集")
    private List<UserAnswers> userAnswers;

    @Schema(description = "用户答案")
    @Data
    public static class UserAnswers {

        @Schema(description = "试题id")
        private String questionId;

        @Schema(description = "用户答案")
        private String userAnswer;
    }
}
