package com.atguigu.examsystem.service.impl;

import com.atguigu.examsystem.entity.Banners;
import com.atguigu.examsystem.mapper.BannersMapper;
import com.atguigu.examsystem.service.BannersService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BannersServiceImpl extends ServiceImpl<BannersMapper, Banners>
        implements BannersService {

    private final BannersMapper bannersMapper;


    public BannersServiceImpl(BannersMapper bannersMapper) {
        this.bannersMapper = bannersMapper;
    }
    @Override
    public void insertBanner(Banners banners) {
        bannersMapper.insertBanner(banners);
    }

    @Override
    public Page<Banners> selectAll(Page<Banners> page, String title, String description) {
        return bannersMapper.selectAll(page, title, description);
    }
}
