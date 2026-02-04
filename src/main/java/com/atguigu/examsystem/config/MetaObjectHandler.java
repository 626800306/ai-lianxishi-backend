package com.atguigu.examsystem.config;

import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MetaObjectHandler implements com.baomidou.mybatisplus.core.handlers.MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("============== 开始插入数据 ==============");
        // 插入"is_active"字段值
        strictInsertFill(metaObject, "isActive", Boolean.class, true);
        // 插入"is_deleted"字段值
        strictInsertFill(metaObject, "isDeleted", Boolean.class, false);
        // 插入"create_time"字段值
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 插入"update_time"字段值
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("============== 开始更新数据 ==============");
        // 更新"update_time"字段值
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
