package com.atguigu.examsystem.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.atguigu.examsystem.dto.BatchRemoveQuestionsDto;
import com.atguigu.examsystem.dto.ExcelPreviewDto;
import com.atguigu.examsystem.entity.QuestionAnswers;
import com.atguigu.examsystem.entity.QuestionChoices;
import com.atguigu.examsystem.entity.Questions;
import com.atguigu.examsystem.enums.QuestionTypeEnum;
import com.atguigu.examsystem.kimi.AiCreateQuestionsDto;
import com.atguigu.examsystem.kimi.KimiAiService;
import com.atguigu.examsystem.mapper.CategoriesMapper;
import com.atguigu.examsystem.mapper.QuestionAnswersMapper;
import com.atguigu.examsystem.mapper.QuestionChoicesMapper;
import com.atguigu.examsystem.mapper.QuestionsMapper;
import com.atguigu.examsystem.service.QuestionsService;
import com.atguigu.examsystem.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuestionsServiceImpl extends ServiceImpl<QuestionsMapper, Questions> implements QuestionsService {


    private final QuestionsMapper questionsMapper;

    private final QuestionChoicesMapper questionChoicesMapper;

    private final QuestionAnswersMapper questionAnswersMapper;

    private final CategoriesMapper categoriesMapper;

    private final KimiAiService kimiAiService;

    public QuestionsServiceImpl(QuestionsMapper questionsMapper,
                                QuestionChoicesMapper questionChoicesMapper,
                                QuestionAnswersMapper questionAnswersMapper,
                                CategoriesMapper categoriesMapper, KimiAiService kimiAiService) {

        this.questionsMapper = questionsMapper;
        this.questionChoicesMapper = questionChoicesMapper;
        this.questionAnswersMapper = questionAnswersMapper;
        this.categoriesMapper = categoriesMapper;
        this.kimiAiService = kimiAiService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveQuestion(Questions question) {
        Questions ques = createQuestion(question);
        questionsMapper.insert(ques);

        // 创建QuestionsChoices对象 选择题
        if (QuestionTypeEnum.CHOICE.getType().equals(question.getType())) {
            List<QuestionChoices> questionChoicesList = question.getChoices().stream().map(choices -> {
                QuestionChoices questionChoices = new QuestionChoices();
                questionChoices.setQuestionId(ques.getId());
                questionChoices.setContent(choices.getContent());
                questionChoices.setIsCorrect(choices.getIsCorrect());
                return questionChoices;
            }).collect(Collectors.toList());
            // 批量插入题目选项
            questionChoicesMapper.insert(questionChoicesList);
        }


        // 创建QuestionAnswer对象 简答题/判断题
        if (QuestionTypeEnum.JUDGE.getType().equals(question.getType()) ||
                QuestionTypeEnum.TEXT.getType().equals(question.getType())) {
            // 创建QuestionAnswers对象
            QuestionAnswers questionAnswers = new QuestionAnswers();
            Questions.Answer answer = question.getAnswer();
            if (answer != null) {
                questionAnswers.setQuestionId(ques.getId());
                questionAnswers.setAnswer(answer.getAnswer());
                questionAnswersMapper.insert(questionAnswers);
            }
        }


    }

    @Override
    public Page<Questions> questionsPage(Page<Questions> page, String keyword, String type, String difficulty, Long categoryId) {
        Page<Questions> p = questionsMapper.questionsPage(page, keyword, type, difficulty, categoryId);
        List<Questions> records = p.getRecords();
        if (!CollUtil.isEmpty(records)) {

            records.stream().forEach(q -> {
                Long questionId = q.getId();
                // 选择题
                if (QuestionTypeEnum.CHOICE.getType().equals(q.getType())) {
                    LambdaQueryWrapper<QuestionChoices> wrapper = Wrappers.lambdaQuery(QuestionChoices.class)
                            .eq(QuestionChoices::getQuestionId, questionId);
                    List<QuestionChoices> questionChoices = questionChoicesMapper.selectList(wrapper);
                    if (!CollUtil.isEmpty(questionChoices)) {
                        List<Questions.Choices> choicesList = questionChoices.stream().map(questionChoice -> {
                            Questions.Choices choices = new Questions.Choices();
                            choices.setContent(questionChoice.getContent());
                            choices.setIsCorrect(questionChoice.getIsCorrect());
                            return choices;
                        }).toList();

                        q.setChoices(choicesList);
                    }
                    // 简答题和判断题
                } else if (QuestionTypeEnum.TEXT.getType().equals(q.getType()) ||
                        QuestionTypeEnum.JUDGE.getType().equals(q.getType())) {
                    LambdaQueryWrapper<QuestionAnswers> wrapper = Wrappers.lambdaQuery(QuestionAnswers.class).eq(QuestionAnswers::getQuestionId, questionId);
                    List<QuestionAnswers> questionAnswers = questionAnswersMapper.selectList(wrapper);
                    if (!CollUtil.isEmpty(questionAnswers)) {
                        QuestionAnswers answer = questionAnswers.stream().findFirst().get();
                        q.setAnswer(new Questions.Answer(answer.getAnswer()));
                    }
                }

            });
        }
        return p;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeQuestionById(Long id) {
        // 删除题目
        removeById(id);
        // 删除题目选项
        LambdaQueryWrapper<QuestionChoices> wrapper = new LambdaQueryWrapper<QuestionChoices>().eq(QuestionChoices::getQuestionId, id);
        questionChoicesMapper.delete(wrapper);
        // 删除题目答案
        LambdaQueryWrapper<QuestionAnswers> wrap = new LambdaQueryWrapper<QuestionAnswers>().eq(QuestionAnswers::getQuestionId, id);
        questionAnswersMapper.delete(wrap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestion(BatchRemoveQuestionsDto dto) {
        List<Long> ids = dto.getIds();
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 批量删除题目
        questionsMapper.deleteByIds(ids);
        // 批量删除题目选项
        LambdaQueryWrapper<QuestionChoices> wrapper = new LambdaQueryWrapper<QuestionChoices>().in(QuestionChoices::getQuestionId, ids);
        questionChoicesMapper.delete(wrapper);
        // 批量删除题目答案
        LambdaQueryWrapper<QuestionAnswers> wrap = new LambdaQueryWrapper<QuestionAnswers>().in(QuestionAnswers::getQuestionId, ids);
        questionAnswersMapper.delete(wrap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestionByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 批量删除题目
        questionsMapper.deleteByIds(ids);
        // 批量删除题目选项
        LambdaQueryWrapper<QuestionChoices> wrapper = new LambdaQueryWrapper<QuestionChoices>().in(QuestionChoices::getQuestionId, ids);
        questionChoicesMapper.delete(wrapper);
        // 批量删除题目答案
        LambdaQueryWrapper<QuestionAnswers> wrap = new LambdaQueryWrapper<QuestionAnswers>().in(QuestionAnswers::getQuestionId, ids);
        questionAnswersMapper.delete(wrap);
    }

    /**
     * 创建一个新的Questions
     *
     * @param question
     * @return
     */
    private Questions createQuestion(Questions question) {
        // 创建一个新的Questions对象
        Questions ques = new Questions();
        ques.setTitle(question.getTitle());
        ques.setType(question.getType());
        ques.setMulti(question.getMulti());
        ques.setCategoryId(question.getCategoryId());
        ques.setDifficulty(question.getDifficulty());
        ques.setScore(question.getScore());
        ques.setAnalysis(question.getAnalysis());

        return ques;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> importQuestions(List<ExcelPreviewDto> excelPreviewDtoList) {
        if (CollUtil.isEmpty(excelPreviewDtoList)) {
            return Result.errorMsg("请传入题目数据");
        }
        // 对请求参数进行处理
        excelPreviewDtoList.stream().forEach(dto -> {
            // 题目内容
            String title = dto.getTitle();
            // 题目类型
            String type = dto.getType();
            // 题目分值
            String score = dto.getScore();
            // 题目分类id
            String categoryId = dto.getCategoryId();
            // 是否多选
            Boolean multi = dto.getMulti();
            // 题目难度
            String difficulty = dto.getDifficulty();
            // 题目解析
            String analysis = dto.getAnalysis();
            // 题目选项
            List<Questions.Choices> choices = dto.getChoices();
            // 题目答案
            Questions.Answer answer = dto.getAnswer();


            // 插入题目
            Questions questions = new Questions();
            questions.setTitle(title);
            questions.setType(type);
            questions.setMulti(multi);
            questions.setCategoryId(Long.parseLong(categoryId));
            questions.setDifficulty(difficulty);
            questions.setScore(Integer.parseInt(score));
            questions.setAnalysis(analysis);
            questionsMapper.insert(questions);

            // 选择题
            if (QuestionTypeEnum.CHOICE.getType().equals(type.trim())) {
                List<QuestionChoices> questionChoicesList = choices.stream().map(choice -> {
                    QuestionChoices questionChoices = new QuestionChoices();
                    questionChoices.setQuestionId(questions.getId());
                    questionChoices.setContent(choice.getContent());
                    questionChoices.setIsCorrect(choice.getIsCorrect());
                    return questionChoices;
                }).toList();
                // 批量插入题目选项
                questionChoicesMapper.insert(questionChoicesList);
            }
            // 判断题和简答题
            if (QuestionTypeEnum.JUDGE.getType().equals(type.trim()) ||
                    (QuestionTypeEnum.TEXT.getType().equals(type.trim()))) {
                QuestionAnswers questionAnswers = new QuestionAnswers();
                questionAnswers.setQuestionId(questions.getId());
                String ans = answer.getAnswer();
                // 判断题只能处理TRUE/FALSE, 简答题可以直接存储。这里是逻辑
                if ("TRUE".equals(ans.trim().toUpperCase())) {
                    questionAnswers.setAnswer("TRUE");
                } else if ("FALSE".equals(ans.trim().toUpperCase())) {
                    questionAnswers.setAnswer("FALSE");
                } else {
                    questionAnswers.setAnswer(ans);
                }
                // 插入题目答案
                questionAnswersMapper.insert(questionAnswers);
            }
        });
        return Result.okMsg("导入成功");
    }

    @Override
    public List<ExcelPreviewDto> previewExcel(MultipartFile file) {
        List<ExcelPreviewDto> excelPrivicwList = CollUtil.newArrayList();
        try {
            ExcelUtil.readBySax(file.getInputStream(), 0, new RowHandler() {
                @Override
                public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
                    // 跳过标题行和第一行示例行
                    if (rowIndex < 2) {
                        return;
                    }
                    log.info("sheetIndex: {}, rowIndex: {}, rowData: {}", sheetIndex, rowIndex, JSONUtil.toJsonStr(rowCells));
                    ExcelPreviewDto dto = new ExcelPreviewDto();
                    dto.setTitle(rowCells.get(0).toString());
                    String type = rowCells.get(1).toString();
                    String multi = rowCells.get(2).toString();
                    if (multi.trim().equals("是")) {
                        dto.setMulti(true);
                    } else if (multi.trim().equals("否")) {
                        dto.setMulti(false);
                    }
                    dto.setCategoryId(rowCells.get(3).toString());
                    dto.setType(type);
                    // 选择题处理逻辑
                    if (QuestionTypeEnum.CHOICE.getType().equals(type)) {
                        String rightAnswer = rowCells.get(10).toString();
                        // 单选和多选处理逻辑
                        String[] splitRightAnswer = rightAnswer.split("、");
                        Questions.Choices a = new Questions.Choices();
                        a.setContent(rowCells.get(6).toString());
                        List<String> list = Arrays.asList(splitRightAnswer);
                        if (list.contains("A")) {
                            a.setIsCorrect(true);
                        } else {
                            a.setIsCorrect(false);
                        }
                        Questions.Choices b = new Questions.Choices();
                        b.setContent(rowCells.get(7).toString());
                        if (list.contains("B")) {
                            b.setIsCorrect(true);
                        } else {
                            b.setIsCorrect(false);
                        }
                        Questions.Choices c = new Questions.Choices();
                        c.setContent(rowCells.get(8).toString());
                        if (list.contains("C")) {
                            c.setIsCorrect(true);
                        } else {
                            c.setIsCorrect(false);
                        }
                        Questions.Choices d = new Questions.Choices();
                        d.setContent(rowCells.get(9).toString());
                        if (list.contains("D")) {
                            d.setIsCorrect(true);
                        } else {
                            d.setIsCorrect(false);
                        }

                        dto.setChoices(CollUtil.newArrayList(a, b, c, d));
                        // 判断题处理逻辑
                    } else if (QuestionTypeEnum.JUDGE.getType().equals(type)) {
                        Questions.Answer answer = new Questions.Answer();
                        answer.setAnswer(rowCells.get(10).toString());
                        dto.setAnswer(answer);
                        // 简单题处理逻辑
                    } else if (QuestionTypeEnum.TEXT.getType().equals(type)) {
                        Questions.Answer answer = new Questions.Answer(rowCells.get(10).toString());
                        dto.setAnswer(answer);
                    }
                    dto.setDifficulty(rowCells.get(4).toString());
                    dto.setScore(rowCells.get(5).toString());
                    dto.setAnalysis(rowCells.get(11).toString());
                    excelPrivicwList.add(dto);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return excelPrivicwList;
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) {
        // 题目内容	题目类型（CHOICE/JUDGE/TEXT）	是否多选（是/否）	分类ID	难度（EASY/MEDIUM/HARD）	分值	选项A	选项B	选项C	选项D	正确答案	解析
        //示例-这一行不能删除	CHOICE	否	14	EASY	5	A	B	C	D	D	这是解析
        List<String> r1 = CollUtil.newArrayList("题目内容", "题目类型（CHOICE/JUDGE/TEXT）", "是否多选（是/否）", "分类ID", "难度（EASY/MEDIUM/HARD）", "分值", "选项A", "选项B", "选项C", "选项D", "正确答案(判断题:TRUE/FALSE;选择题:单选,A;多选,A、B、C)", "解析");
        List<String> r2 = CollUtil.newArrayList("示例-这一行不能删除", "CHOICE", "否", "14", "EASY", "5", "A", "B", "C", "D", "D", "这是解析");
        List<List<String>> rows = CollUtil.newArrayList(r1, r2);
        // 创建ExcelWriter对象
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 强制写入标题头
        writer.write(rows, true);

        try {
            // 下面一行代码是.xls格式
//            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            // 这一行是.xlsx格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("题目导入模板.xlsx", CharsetUtil.UTF_8));

            // 获取输出流
            ServletOutputStream out = response.getOutputStream();
            // 将数据刷新到out流
            writer.flush(out);
            // 关闭writer,释放内存
            writer.close();
            // 关联servlet流
            IoUtil.close(out);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public JSONArray callKimiAi(AiCreateQuestionsDto dto) {
        // 构建提示词
        String prompt = buildPrompt(dto);
        // 调用Kimi AI
        String kimiRes = kimiAiService.callKimiAi(prompt);

        JSONObject jsonObject = JSONUtil.parseObj(kimiRes);
        JSONObject jo = JSONUtil.parseObj(jsonObject.getByPath("choices[0].message.content"));
        JSONArray array = (JSONArray) jo.get("questions");
        return array;
    }

    private String buildPrompt(AiCreateQuestionsDto dto) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("请为我生成").append(dto.getCount()).append("道关于【")
                .append(dto.getTopic()).append("】的题目。\n\n");

        prompt.append("要求：\n");

        // 题目类型要求
        if (dto.getTypes() != null && !dto.getTypes().isEmpty()) {
            List<String> typeList = Arrays.asList(dto.getTypes().split(","));
            prompt.append("- 题目类型：");
            for (String type : typeList) {
                switch (type.trim()) {
                    case "CHOICE":
                        prompt.append("选择题");
                        if (dto.getIncludeMultiple() != null && dto.getIncludeMultiple()) {
                            prompt.append("(包含单选和多选)");
                        }
                        prompt.append(" ");
                        break;
                    case "JUDGE":
                        prompt.append("判断题（**重要：确保正确答案和错误答案的数量大致平衡，不要全部都是正确或错误**） ");
                        break;
                    case "TEXT":
                        prompt.append("简答题 ");
                        break;
                }
            }
            prompt.append("\n");
        }

        // 难度要求
        if (dto.getDifficulty() != null) {
            String difficultyText = switch (dto.getDifficulty()) {
                case "EASY" -> "简单";
                case "MEDIUM" -> "中等";
                case "HARD" -> "困难";
                default -> "中等";
            };
            prompt.append("- 难度等级：").append(difficultyText).append("\n");
        }

        // 额外要求
        if (dto.getRequirements() != null && !dto.getRequirements().isEmpty()) {
            prompt.append("- 特殊要求：").append(dto.getRequirements()).append("\n");
        }

        // 判断题特别要求
        if (dto.getTypes() != null && dto.getTypes().contains("JUDGE")) {
            prompt.append("- **判断题特别要求**：\n");
            prompt.append("  * 确保生成的判断题中，正确答案(TRUE)和错误答案(FALSE)的数量尽量平衡\n");
            prompt.append("  * 不要所有判断题都是正确的或都是错误的\n");
            prompt.append("  * 错误的陈述应该是常见的误解或容易混淆的概念\n");
            prompt.append("  * 正确的陈述应该是重要的基础知识点\n");
        }

        prompt.append("\n请严格按照以下JSON格式返回，不要包含任何其他文字：\n");
        prompt.append("\n");
        prompt.append("{\n");
        prompt.append("  \"questions\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"title\": \"题目内容\",\n");
        prompt.append("      \"type\": \"CHOICE|JUDGE|TEXT\",\n");
        prompt.append("      \"multi\": true/false,\n");
        prompt.append("      \"difficulty\": \"EASY|MEDIUM|HARD\",\n");
        prompt.append("      \"score\": 5,\n");
        prompt.append("      \"choices\": [\n");
        prompt.append("        {\"content\": \"选项内容\", \"isCorrect\": true/false, \"sort\": 1}\n");
        prompt.append("      ],\n");
        prompt.append("      \"answer\": \"TRUE或FALSE(判断题专用)|文本答案(简答题专用)\",\n");
        prompt.append("      \"analysis\": \"题目解析\"\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
        prompt.append("\n\n");

        prompt.append("注意：\n");
        prompt.append("1. 选择题必须有choices数组，判断题和简答题设置answer字段\n");
        prompt.append("2. 多选题的multi字段设为true，单选题设为false\n");
        prompt.append("3. 判断题的answer字段只能是\"TRUE\"或\"FALSE\"，请确保答案分布合理\n");
        prompt.append("4. 每道题都要有详细的解析\n");
        prompt.append("5. 题目要有实际价值，贴近实际应用场景\n");
        prompt.append("6. 严格按照JSON格式返回，确保可以正确解析\n");

        // 如果只生成判断题，额外强调答案平衡
        if (dto.getTypes() != null && dto.getTypes().equals("JUDGE") && dto.getCount() > 1) {
            prompt.append("7. **判断题答案分布要求**：在").append(dto.getCount()).append("道判断题中，");
            int halfCount = dto.getCount() / 2;
            if (dto.getCount() % 2 == 0) {
                prompt.append("请生成").append(halfCount).append("道正确(TRUE)和").append(halfCount).append("道错误(FALSE)的题目");
            } else {
                prompt.append("请生成约").append(halfCount).append("-").append(halfCount + 1).append("道正确(TRUE)和约").append(halfCount).append("-").append(halfCount + 1).append("道错误(FALSE)的题目");
            }
        }

        return prompt.toString();
    }
}
