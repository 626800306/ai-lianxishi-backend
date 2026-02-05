package com.atguigu.examsystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "考试记录表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("exam_records")
public class ExamRecords extends BaseEntity {

    @Schema(description = "试卷id")
    @TableField("exam_id")
    private Long examId;

    @Schema(description = "考生姓名")
    @TableField("student_name")
    private String studentName;

    @Schema(description = "考生总得分")
    @TableField("score")
    private Integer score;

    @Schema(description = "答案")
    @TableField("answers")
    private String answers;

    @Schema(description = "考试开始时间")
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @Schema(description = "考试结束时间")
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    @Schema(description = "试卷状态")
    @TableField("status")
    private String status;

    @Schema(description = "windows切屏次数")
    @TableField("window_switches")
    private Integer windowSwitches;

    @Schema(description = "考试时长")
    @TableField(exist = false)
    private Integer duration;

    @Schema(description = "试卷信息")
    @TableField(exist = false)
    private Paper paper;

    @Schema(description = "题目总数")
    @TableField(exist = false)
    private Integer totalQuestions;
}
