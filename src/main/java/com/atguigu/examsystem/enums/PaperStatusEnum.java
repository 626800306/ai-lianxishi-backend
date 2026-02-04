package com.atguigu.examsystem.enums;

public enum PaperStatusEnum {

    DRAFT("DRAFT", "草稿"),
    PUBLISHED("PUBLISHED", "发布"),
    ;

    private final String status;
    private final String desc;

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    PaperStatusEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
