package com.atguigu.examsystem.kimi;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;

@Service
public class KimiAiService {

    private final KimiConfig kimiConfig;

    public KimiAiService(KimiConfig kimiConfig) {
        this.kimiConfig = kimiConfig;
    }

    /**
     * JSONArray array = new JSONArray();
     *         JSONObject j1 = new JSONObject();
     *         j1.set("role", "system");
     *         j1.set("content", "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。");
     *         array.add(j1);
     *         JSONObject j2 = new JSONObject();
     *         j2.set("role", "user");
     *         j2.set("content", "你好，我叫李雷。请帮我计算一下1+1等于多少?");
     *         array.add(j2);
     *         JSONObject obj = new JSONObject();
     *         obj.set("model", "kimi-k2-turbo-preview");
     *         obj.set("messages", array);
     *         obj.set("temperature", 0.6);
     *
     *         HttpResponse response = HttpUtil.createPost("https://api.moonshot.cn/v1/chat/completions")
     *                 .header("Content-Type", "application/json")
     *                 .header("Authorization", "Bearer sk-Hr6rvdN72tLRMmVxAugHwVsWfCfROygf0KNfJIG4xE7OdcPC")
     *                 .body(JSONUtil.toJsonStr(obj))
     *                 .execute();
     *         String body = response.body();
     *
     *         System.out.println(body);
     * @param prompt
     * @return
     */
    public String callKimiAi(String prompt) {
        JSONArray array = new JSONArray();
        // 设置系统角色
        JSONObject j1 = new JSONObject();
        j1.set("role", "system");
        j1.set("content", "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。");
        array.add(j1);
        // 设置用户角色
        JSONObject j2 = new JSONObject();
        j2.set("role", "user");
        j2.set("content", prompt);
        array.add(j2);
        // 组装请求参数
        JSONObject param = new JSONObject();
        param.set("model", kimiConfig.getModel());
        param.set("messages", array);
        param.set("temperature", kimiConfig.getTemperature());

        HttpResponse response = HttpUtil.createPost(kimiConfig.getBaseUrl())
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + kimiConfig.getApiKey())
                .body(JSONUtil.toJsonStr(param))
                .execute();

        // 返回body主体
        return response.body();
    }

    /**
     *{
     *   "id": "chatcmpl-69857a823144317f49f6b28d",
     *   "object": "chat.completion",
     *   "created": 1770355330,
     *   "model": "kimi-k2-turbo-preview",
     *   "choices": [
     *     {
     *       "index": 0,
     *       "message": {
     *         "role": "assistant",
     *         "content": "本次考试得分17/20，正确率75%，表现优良，基础扎实。优势在于核心题型掌握牢固，失分主要在第2题计算细节与第4题审题偏差，暴露粗心与综合迁移不足。建议：每日限时练10分钟易错点，做题前划关键条件，完成后反向验算；周末做1道同类拓展题并写错因一句话，培养检查习惯。坚持两周，细节分即可提升。老师相信你再专注一点，下次定能突破18分，继续加油！"
     *       },
     *       "finish_reason": "stop"
     *     }
     *   ],
     *   "usage": {
     *     "prompt_tokens": 171,
     *     "completion_tokens": 109,
     *     "total_tokens": 280
     *   }
     * }
     * @param body
     * @return 返回kimi AI中content内容
     */
    public String kimiBodyResContent(String body) {
        JSONObject jsonObject = JSONUtil.parseObj(body);
        JSONArray array = (JSONArray) jsonObject.get("choices");
        JSONObject jo = (JSONObject) array.get(0);
        JSONObject jot = (JSONObject) jo.get("message");
        return jot.get("content").toString();
    }
}
