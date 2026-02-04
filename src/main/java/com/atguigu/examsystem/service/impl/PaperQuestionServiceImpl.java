package com.atguigu.examsystem.service.impl;

import com.atguigu.examsystem.entity.PaperQuestion;
import com.atguigu.examsystem.mapper.PaperQuestionMapper;
import com.atguigu.examsystem.service.PaperQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PaperQuestionServiceImpl extends ServiceImpl<PaperQuestionMapper, PaperQuestion> implements PaperQuestionService {
}
