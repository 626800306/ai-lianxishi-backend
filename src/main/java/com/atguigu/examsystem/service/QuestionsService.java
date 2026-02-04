package com.atguigu.examsystem.service;

import cn.hutool.json.JSONArray;
import com.atguigu.examsystem.dto.BatchRemoveQuestionsDto;
import com.atguigu.examsystem.dto.ExcelPreviewDto;
import com.atguigu.examsystem.entity.Questions;
import com.atguigu.examsystem.kimi.AiCreateQuestionsDto;
import com.atguigu.examsystem.vo.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionsService extends IService<Questions> {

    void saveQuestion(Questions question);

    Page<Questions> questionsPage(Page<Questions> page, String keyword, String type, String difficulty, Long categoryId);

    void removeQuestionById(Long id);

    void batchRemoveQuestion(BatchRemoveQuestionsDto dto);

    void batchRemoveQuestionByIds(List<Long> ids);

    Result<String> importQuestions(List<ExcelPreviewDto> excelPreviewDtoList);

    List<ExcelPreviewDto> previewExcel(MultipartFile file);

    void downloadTemplate(HttpServletResponse response);

    JSONArray callKimiAi(AiCreateQuestionsDto dto);
}
