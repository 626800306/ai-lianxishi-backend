package com.atguigu.examsystem.constant;

/**
 * redis常量
 */
public class RedisConstants {

    public static final String QUESTION = "question";

    public static final String PAPER = "paper";

    public static final String EXAM_RECORD = "exam_record";

    public static final String QUESTION_DETAIL = "question:detail:";

    public static final String QUESTION_CATEGORY = "question:category:";

    public static final String PAPER_DETAIL = "paper:detail:";

    public static final String EXAM_RECORD_DETAIL = "exam_record:detail:";

    public static final String QUESTION_POPULAR = "question:popular";

    public static final String QUESTION_VIEW_COUNT = "question:view_count";

    public static final Integer QUESTION_POPULAR_COUNT = 10;

    // 默认过期时间1800秒(30分钟)
    public static final Integer DEFAULT_EXPIRE_SECONDS = 1800;

    // 热门数据过期时间3600秒(1小时)
    public static final Integer HOT_DATA_EXPIRE_SECONDS = 3600;


}
