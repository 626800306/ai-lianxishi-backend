package com.atguigu.examsystem.enums;

public enum QuestionTypeEnum {
    // 选择题
    CHOICE("CHOICE", "选择题"),
    // 判断题
    JUDGE("JUDGE", "判断题"),
    // 简答题
    TEXT("TEXT", "简答题"),
    ;

    QuestionTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private final String type;

    private final String desc;

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
