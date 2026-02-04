package com.atguigu.examsystem.enums;

public enum QuestionDifficultyEnum {
    EASY("EASY", "简单题"),
    MEDIUM("MEDIUM", "中等题"),
    HARD("HARD", "困难题"),
    ;

    private final String difficulty;
    private final String desc;


    QuestionDifficultyEnum(String difficulty, String desc) {
        this.difficulty = difficulty;
        this.desc = desc;
    }


    public String getDifficulty() {
        return difficulty;
    }

    public String getDesc() {
        return desc;
    }
}
