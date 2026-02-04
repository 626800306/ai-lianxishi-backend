package com.atguigu.examsystem.service.impl;

import com.atguigu.examsystem.entity.QuestionAnswers;
import com.atguigu.examsystem.mapper.QuestionAnswersMapper;
import com.atguigu.examsystem.service.QuestionAnswersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class QuestionAnswersServiceImpl extends ServiceImpl<QuestionAnswersMapper, QuestionAnswers>
        implements QuestionAnswersService {
}
