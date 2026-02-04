package com.atguigu.examsystem.enums;


public enum ExamRecordsEnum {

    UNDERWAY("UNDERWAY", "进行中"),
    COMPLETED("COMPLETED", "已完成"),
    APPROVED("APPROVED", "已批阅"),
    ;

    private final String type;
    private final String desc;

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    ExamRecordsEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
