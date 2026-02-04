package com.atguigu.examsystem.mapper;

import com.atguigu.examsystem.entity.Questions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionsMapper extends BaseMapper<Questions> {

    Page<Questions> questionsPage(@Param("page") Page<Questions> page,
                                  @Param("keyword") String keyword,
                                  @Param("type") String type,
                                  @Param("difficulty") String difficulty,
                                  @Param("categoryId") Long categoryId);
}
