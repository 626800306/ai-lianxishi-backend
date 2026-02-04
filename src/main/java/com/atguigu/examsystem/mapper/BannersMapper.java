package com.atguigu.examsystem.mapper;

import com.atguigu.examsystem.entity.Banners;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BannersMapper extends BaseMapper<Banners> {

    void insertBanner(Banners banners);

    Page<Banners> selectAll(@Param("page") Page<Banners> page,
                            @Param("title") String title,
                            @Param("description") String description);
}
