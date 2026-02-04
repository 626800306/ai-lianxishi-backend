package com.atguigu.examsystem.service;

import com.atguigu.examsystem.entity.Banners;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface BannersService extends IService<Banners> {

    void insertBanner(Banners banners);

    Page<Banners> selectAll(Page<Banners> page, String title, String description);
}
