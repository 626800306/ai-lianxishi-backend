package com.atguigu.examsystem;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.NodeParser;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.*;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.word.Word07Writer;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.atguigu.examsystem.kimi.KimiAiService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.List;
@Slf4j
@SpringBootTest
class ExamSystemApplicationTests {

    @Autowired
    private KimiAiService kimiAiService;

    @Test
    void contextLoads() {
        /*java.util.List list = baseMapper.selectList(null);
        System.out.println(list);*/
        // 构建node列表
        List<TreeNode<String>> nodeList = CollUtil.newArrayList();
        TreeNode<String> t1 = new TreeNode<>("13", "0", "选择题", 1);
        t1.setExtra(Map.of("sort", 1, "create_time", "2025-07-23 14:17:32", "is_deleted", 0));
        nodeList.add(t1);
        TreeNode<String> t2 = new TreeNode<>("16", "13", "Java基础", 1);
        t2.setExtra(Map.of("sort", 2, "create_time", "2025-07-23 11:11:11", "is_deleted", 0));
        nodeList.add(t2);
        TreeNode<String> t3 = new TreeNode<>("18", "16", "多线程", 1);
        t3.setExtra(Map.of("sort", 3, "create_time", "2025-07-23 22:22:22", "is_deleted", 0));
        nodeList.add(t3);
        TreeNode<String> t4 = new TreeNode<>("20", "18", "thread", 1);
        t4.setExtra(Map.of("sort", 4, "create_time", "2025-07-23 33:33:33", "is_deleted", 0));
        nodeList.add(t4);
        nodeList.add(new TreeNode<>("6", "0", "填空题", 1));
        nodeList.add(new TreeNode<>("7", "0", "简单题", 1));

        // 0表示最顶层的id是0
//        List<Tree<String>> treeList = TreeUtil.build(nodeList, "0");


        TreeNodeConfig config = new TreeNodeConfig()
                .setWeightKey("sort");

        List<Tree<String>> treeList = TreeUtil.build(nodeList, "0", config, new NodeParser<TreeNode<String>, String>() {
            @Override
            public void parse(TreeNode<String> treeNode, Tree<String> tree) {
                tree.setId(treeNode.getId());
                tree.setName(treeNode.getName());
                tree.setParentId(treeNode.getParentId());
                if (treeNode.getExtra() != null) {
                    String sort = treeNode.getExtra().get("sort").toString();
                    tree.setWeight(sort);
                    String createTime = treeNode.getExtra().get("create_time").toString();
                    tree.putExtra("createTime", createTime);
                    String isDeleted = treeNode.getExtra().get("is_deleted").toString();
                    tree.putExtra("isDeleted", isDeleted);
                }

            }
        });

        System.out.println(treeList);
    }

    @Test
    public void test() {
        // 构建node列表
        List<TreeNode<Integer>> nodeList = CollUtil.newArrayList();
        TreeNode t1 = new TreeNode(1, 0, "选择题", 0);
        t1.setExtra(Map.of("create_time", "2025-07-23 11:11:11", "update_time", "2025-07-23 11:11:11"));
        nodeList.add(t1);

        TreeNode t2 = new TreeNode(2, 1, "单选题", 0);
        t2.setExtra(Map.of("create_time", "2025-07-23 22:22:22", "update_time", "2025-07-23 22:22:22"));
        nodeList.add(t2);

        TreeNode t3 = new TreeNode(3, 2, "A、B选项只能选一个", 0);
        t3.setExtra(Map.of("create_time", "2025-07-23 33:33:33", "update_time", "2025-07-23 33:33:33"));
        nodeList.add(t3);

        TreeNode t4 = new TreeNode(4, 2, "C、D选项只能选一个", 0);
        t4.setExtra(Map.of("create_time", "2025-07-23 44:44:44", "update_time", "2025-07-23 44:44:44"));
        nodeList.add(t4);

        TreeNode t5 = new TreeNode(5, 4, "C选项只能选一个", 0);
        t5.setExtra(Map.of("create_time", "2025-07-23 12:12:12", "update_time", "2025-07-23 12:12:12"));
        nodeList.add(t5);

        TreeNode t6 = new TreeNode(6, 1, "多选题", 0);
        t6.setExtra(Map.of("create_time", "2025-07-23 55:55:55", "update_time", "2025-07-23 55:55:55"));
        nodeList.add(t6);

        TreeNode t7 = new TreeNode(7, 0, "简答题", 0);
        t7.setExtra(Map.of("create_time", "2025-07-23 23:23:23", "update_time", "2025-07-23 23:23:23"));
        nodeList.add(t7);

        List<Tree<Integer>> treeList = TreeUtil.build(nodeList, 0);
        System.out.println(treeList);
    }

    @Test
    public void bigExcelWriter() {
        // 构建数据
        List<?> a = CollUtil.newArrayList("a", "b", "c", "d", DateUtil.now(), 1.2345668898);
        ArrayList<?> a1 = CollUtil.newArrayList("a1", "b1", "c1", "d1", DateUtil.now(), 23.3456689);
        List<?> a2 = CollUtil.newArrayList("a2", "b2", "c2", "d2", DateUtil.now(), 3.345678909);
        List<? extends Serializable> a3 = CollUtil.newArrayList("a3", "b3", "c3", "d3", DateUtil.now(), 4.568909);
        // 组装所有行数据
        List<List<?>> rows = CollUtil.newArrayList(a, a1, a2, a3);
        // 创建BigExcelWriter
        try (BigExcelWriter writer = ExcelUtil.getBigWriter("C:\\Users\\Administrator\\Desktop\\test.xls")) {
            // 合并单元格后的标题行，使用默认标题样式
            writer.merge(a.size() - 1, "测试数据");
            // 写出数据，使用默认样式
            writer.write(rows, true);
            // 关闭writer，释放内存
            writer.close();
        }

    }


    @Test
    public void docs() {
//        Word07Writer writer = WordUtil.getWriter(FileUtil.file("C:\\Users\\Administrator\\Desktop\\test.doc"));
        try (Word07Writer writer = new Word07Writer(FileUtil.file("C:\\Users\\Administrator\\Desktop\\test.doc"))) {
            // 添加段落（标题）
            writer.addText(new Font("方正小标宋简体", Font.PLAIN, 22), "我是第一部分", "我是第二部分");
            // 添加段落（正文）
            writer.addText(new Font("宋体", Font.PLAIN, 22), "我是正文第一部分", "我是正文第二部分");
            // 写出到文件
            writer.flush();
            // 关闭
            writer.close();
        }

    }

    @Test
    public void testEasyExcel() {
        // 生成文件名 根据业务特点生成
        String filePath = "C:\\Users\\Administrator\\Desktop\\" + System.currentTimeMillis() + ".xlsx";
        // 写法1
        EasyExcel.write(filePath, DemoData.class)
                .sheet("模版1")
                .doWrite(data());
    }

    @Test
    public void testEasyExcelWrite() {
        // 生成文件名 根据业务特点生成
        String filePath = "C:\\Users\\Administrator\\Desktop\\" + System.currentTimeMillis() + ".xlsx";
        // 生成excelWriter
        ExcelWriter excelWriter = EasyExcel.write(filePath, DemoData.class).build();
        // 生成writeSheet
        WriteSheet writeSheet = EasyExcel.writerSheet("模版1").build();
        // 写入excel数据
        excelWriter.write(data(), writeSheet);
        excelWriter.finish();
    }


    private List<DemoData> data() {
        List<DemoData> list = new ArrayList<>(500);
        for (int i = 0; i < 500; i++) {
            DemoData demoData = new DemoData();
            demoData.setTitle("标题" + i);
            demoData.setDate(DateUtil.date());
            demoData.setNum(0.1111 + i);
            list.add(demoData);
        }
        return list;
    }

    @Test
    public void repeatTest() {
        // 生成文件名 根据业务特点生成
        String filePath = "C:\\Users\\Administrator\\Desktop\\" + System.currentTimeMillis() + ".xlsx";
        // 生成excelWriter 需要指定用到那个class去写
        try (ExcelWriter excelWriter = EasyExcel.write(filePath, DemoData.class).build()) {

            // 这里注意 如果同一个sheet只需要创建一次
            WriteSheet writeSheet = EasyExcel.writerSheet("模版").build();
            // 同一个sheet重复写入多次数据
            for (int i = 0; i < 5; i++) {
                List<DemoData> data = data();
                excelWriter.write(data, writeSheet);
            }
        }
    }

    @Test
    public void repeatTest2() {
        // 生成文件名 根据业务特点生成
        String filePath = "C:\\Users\\Administrator\\Desktop\\" + System.currentTimeMillis() + ".xlsx";
        // 创建excelWriter
        try (ExcelWriter excelWriter = EasyExcel.write(filePath, DemoData.class).build()) {
            for (int i = 0; i < 5; i++) {
                // 每次创建新的writeSheet
                WriteSheet writeSheet = EasyExcel.writerSheet("模版" + i).build();
                // 相同数据
                List<DemoData> data = data();
                // 写入到不同的sheet中
                excelWriter.write(data, writeSheet);
            }
        }

    }

    @Test
    public void testHttp() {
        HttpResponse res = HttpRequest.post("http://www.baidu.com")
                .header("Content-Type", "application/json") // 头信息，多个头信息多次调用这个方法
                .header("Accept", "application/json")
                .timeout(20000) // 超时，毫秒
                .execute();

        byte[] bytes = res.bodyBytes();
        InputStream inputStream = res.bodyStream();
        int status = res.getStatus();
        String body = res.body();
        String header = res.header("Content-Type");
        System.out.println("res:" + res + "status:" + status + " body:" + body + " header:" + header + " bytes.length:" + bytes.length);
    }

    @Test
    public void testHttpUtil() {
        // 远程文件URL
        String fileUrl = "https://github.com/alibaba/easyexcel/archive/refs/tags/v4.0.3.zip";
        // 将文件下载后保存到本地, 返回文件大小
        // long size = HttpUtil.downloadFile(fileUrl, FileUtil.file("C:\\Users\\Administrator\\Desktop\\"));
        long size = HttpUtil.downloadFile(fileUrl, "C:\\Users\\Administrator\\Desktop\\");
        System.out.println("size:" + size);
    }

    @Test
    public void testHttpUtilDownloadProgress() {

        // 远程文件URL
        String fileUrl = "https://github.com/alibaba/easyexcel/archive/refs/tags/v4.0.3.zip";
        // 将文件下载后保存到本地, 返回文件大小
        long size = HttpUtil.downloadFile(fileUrl, FileUtil.file("C:\\Users\\Administrator\\Desktop\\"), new StreamProgress() {
            @Override
            public void start() {
                System.out.println("开始下载。。。。");
            }

            @Override
            public void progress(long total, long progressSize) {
                System.out.println("已下载:" + FileUtil.readableFileSize(progressSize));
            }

            @Override
            public void finish() {
                System.out.println("下载完成。。。。");
            }

        });
        System.out.println("总文件大小:" + FileUtil.readableFileSize(size));
    }


    @Test
    public void JSONObject() {
        /*JSONObject entries = new JSONObject().set("k1", 1);
        JSONObject obj = JSONUtil.createObj().set("k1", "v1").set("k2", "v2");
//        System.out.println(obj);
        JSONObject j1 = new JSONObject("{\"k1\":\"v1\",\"k2\":\"v2\"}");
        JSONObject j2 = JSONUtil.parseObj("{\"k1\":\"v1\",\"k2\":\"v2\"}");
//        System.out.println(obj.toString());
        System.out.println(obj.toStringPretty());*/

        UserA userA = new UserA();
        userA.setName("张三");
        userA.setDate(new Date());
        userA.setSeqs(CollUtil.newArrayList(new UserA.Seq(null), new UserA.Seq("s2")));
        JSONObject obj1 = JSONUtil.parseObj(userA, false, true).setDateFormat("yyyy-MM-dd HH::mm:ss");
//        obj1.setDateFormat("yyyy-MM-dd HH:mm:ss");
        /*System.out.println(obj1.toStringPretty());
        UserA bean1 = JSONUtil.toBean(obj1, UserA.class);
        UserA bean = obj1.toBean(UserA.class);
        System.out.println(bean1);
        System.out.println(bean);*/
        System.out.println(obj1.getByPath("seqs[1].s", String.class));
    }

    @Test
    public void JSONArray() {
        JSONArray array = new JSONArray();
        array.add("a");
        array.add("b");
        array.add("c");
        System.out.println(array.toString());
        System.out.println("=============================================");
        System.out.println(array.toStringPretty());
        System.out.println("=============================================");
        KeyBean k1 = new KeyBean();
        k1.setAkey("ak1");
        k1.setBkey("bk1");
        KeyBean k2 = new KeyBean();
        k2.setAkey("ak2");
        k2.setBkey("bk2");
        JSONArray ja = new JSONArray().set(k1).set(k2);
        JSONArray array1 = JSONUtil.parseArray(CollUtil.newArrayList(k1, k2));
        System.out.println(((JSONObject)array1.get(0)).getStr("akey"));

        /*System.out.println(array1);
        List<KeyBean> list1 = array1.toList(KeyBean.class);
        System.out.println(list1);*/


        List<KeyBean> list = JSONUtil.toList(array1, KeyBean.class);
//        System.out.println(list);

        KeyBean[] array3 = array1.toArray(new KeyBean[0]);
//        System.out.println(array3[0]);


        String s = "{\n" +
                "  \"want\": true,\n" +
                "  \"current\": \"jungle\",\n" +
                "  \"land\": -1595594443,\n" +
                "  \"fell\": [\n" +
                "    true,\n" +
                "    {\n" +
                "      \"log\": false,\n" +
                "      \"joy\": {\n" +
                "        \"steel\": [\n" +
                "          \"tightly\",\n" +
                "          -874716337,\n" +
                "          -606975576,\n" +
                "          2087893022,\n" +
                "          \"declared\",\n" +
                "          \"thy\"\n" +
                "        ],\n" +
                "        \"hunt\": \"shelter\",\n" +
                "        \"vowel\": -1592045728,\n" +
                "        \"welcome\": true,\n" +
                "        \"beside\": \"object\",\n" +
                "        \"review\": \"serve\"\n" +
                "      },\n" +
                "      \"tool\": -1346044134,\n" +
                "      \"north\": true,\n" +
                "      \"brush\": \"remember\",\n" +
                "      \"wet\": -848961050\n" +
                "    }\n" +
                "  ],\n" +
                "  \"claws\": true,\n" +
                "  \"hung\": true\n" +
                "}";

        JSONObject j = new JSONObject(s);
        String current = j.getByPath("current", String.class);
//        System.out.println(current);


        JSONArray array2 = JSONUtil.parseArray("[{'id':1,'name':'zhangsan'},{'id':2,'name':'lisi'}]");
//        System.out.println(array2.getByPath("[0].name", String.class));
//      System.out.println(array2);
    }

    @Test
    public void testJSONUtil() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", "v2");
//        String str = JSONUtil.toJsonStr(map);
//        System.out.println(str);
        String str1 = JSONUtil.toJsonPrettyStr(map);
        System.out.println(str1);
        String str = "{\"k1\":\"v1\",\"k2\":\"v2\"}";
        JSONObject jsonObject = JSONUtil.parseObj(str);
        String v1 = jsonObject.getStr("k1");
        System.out.println(v1);


        String xml = "<root><k1>v1</k1><k2>v2</k2></root>";
        JSONObject json = JSONUtil.parseFromXml(xml);
        Object obj = json.get("root");
        System.out.println(obj);
//        System.out.println(json.get("k1"));

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m1.put("k1", "v1");
        m1.put("k2", "v2");
        m.put("root", m1);
        JSONObject entries = new JSONObject(m);
        String xmlStr = JSONUtil.toXmlStr(entries);
        System.out.println(xmlStr);

//        String quote = JSONUtil.quote("{k1:v1,k2:v2}");
//        System.out.println(quote);
        System.out.println(JSONUtil.formatJsonStr("{k1:v1,k2:v2}"));
    }

    @Test
    public void testKimi() {
        JSONArray array = new JSONArray();
        JSONObject j1 = new JSONObject();
        j1.set("role", "system");
        j1.set("content", "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。");
        array.add(j1);
        JSONObject j2 = new JSONObject();
        j2.set("role", "user");
        j2.set("content", "请问马云的成绩有哪些？");
        array.add(j2);
        JSONObject obj = new JSONObject();
        obj.set("model", "kimi-k2-turbo-preview");
        obj.set("messages", array);
        obj.set("temperature", 0.6);

        HttpResponse response = HttpUtil.createPost("https://api.moonshot.cn/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer sk-Hr6rvdN72tLRMmVxAugHwVsWfCfROygf0KNfJIG4xE7OdcPC")
                .body(JSONUtil.toJsonStr(obj))
                .execute();
        String body = response.body();

        System.out.println(body);
    }


    @Test
    public void callKimi() {
        String res = kimiAiService.callKimiAi("请告诉我，马云的主要成绩有哪些？");
        log.info("kimi响应结果：{}", res);
    }

    @Test
    public void testBeanUtil() {
        System.out.println(BeanUtil.isBean(UserA.class));

        HashMap<String, Object> map = new HashMap<>();
        map.put("NAme", "Joe");
        map.put("aGe", 12);
        map.put("OpenId", "DFDFSDFWERWER");
        User user = BeanUtil.toBean(map, User.class);
        System.out.println(user);

        User user1 = BeanUtil.toBeanIgnoreCase(map, User.class, true);
        User user2 = new User();
        User userA = BeanUtil.copyProperties(user1, User.class);
        BeanUtil.copyProperties(user1, user2);
        System.out.println(userA);
        System.out.println(user2);
    }

    @Test
    public void testRandomUtil() {
        int[] ints = RandomUtil.randomInts(4);
        int i = RandomUtil.randomInt(10, 100);
        System.out.println(i);

        String s = RandomUtil.randomNumbers(4);
        System.out.println(s);
        List<Integer> list = CollUtil.newArrayList(1, 2, 3, 4, 5);
        Integer i1 = RandomUtil.randomEle(list);
        System.out.println(i1);

        char c = RandomUtil.randomChinese();
        System.out.println(c);



    }

}


@Data
class User {
    private String name;
    private Integer age;
    private String openId;
}
@Data
class UserA {
    private String name;

    private String a;

    private Date date;

    private List<Seq> seqs;

    @Data
    @AllArgsConstructor
    static class Seq {
        private String s;
    }
}

@Data
class KeyBean{
    private String akey;
    private String bkey;
}
