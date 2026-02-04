package com.atguigu.examsystem.service;

import com.atguigu.examsystem.dto.AiCreatePaperDto;
import com.atguigu.examsystem.dto.ManualCreatePaperDto;
import com.atguigu.examsystem.entity.Paper;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PaperService extends IService<Paper> {

    void savePaper(ManualCreatePaperDto dto);

    void removePaperById(Long id);

    ManualCreatePaperDto getPaperById(Long id);

    void aiCreatePaper(AiCreatePaperDto dto);
}
