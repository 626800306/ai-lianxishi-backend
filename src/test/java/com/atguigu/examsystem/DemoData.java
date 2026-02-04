package com.atguigu.examsystem;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DemoData {

    @ExcelProperty(value = "标题", index = 0)
    private String title;

    @ExcelProperty(value = "日期", index = 1)
    @DateTimeFormat(value = "yyyy年MM月dd HH时mm分ss秒")
    private Date date;

    @ExcelProperty(value = "数值", index = 2)
    @NumberFormat("#.##%")
    private Double num;

    @ExcelIgnore
    private String ignore;
}
