package com.atguigu.examsystem.service.impl;

import com.atguigu.examsystem.entity.QuestionChoices;
import com.atguigu.examsystem.mapper.QuestionChoicesMapper;
import com.atguigu.examsystem.service.QuestionChoicesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class QuestionChoicesServiceImpl extends ServiceImpl<QuestionChoicesMapper, QuestionChoices>
    implements QuestionChoicesService {
}
