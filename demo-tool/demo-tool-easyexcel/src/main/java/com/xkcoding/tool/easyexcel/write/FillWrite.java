package com.xkcoding.tool.easyexcel.write;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.xkcoding.tool.easyexcel.data.MockData;
import com.xkcoding.tool.easyexcel.utils.FileUtils;

public class FillWrite {

  public static void main(String[] args) {
    listFill();
  }
  /**
   * 填充列表
   *
   * @since 2.1.1
   */
  public static void listFill() {
    // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
    // 填充list 的时候还要注意 模板中{.} 多了个点 表示list
    // 如果填充list的对象是map,必须包涵所有list的key,哪怕数据为null，必须使用map.put(key,null)

    // 方式 1
    // listOne();

    // 方式 2
    listTwo();
  }
  public static void listOne() {
    String templateFileName = FileUtils.getPath() + "listTemp.xlsx";

    // 方案1 一下子全部放到内存里面 并填充
    String fileName = FileUtils.getPath() + "listFill" + System.currentTimeMillis() + ".xlsx";
    // 这里 会填充到第一个sheet， 然后文件流会自动关闭
    EasyExcel.write(fileName).withTemplate(templateFileName).sheet().doFill(MockData.data());
  }


  public static void listTwo(){
    String templateFileName = FileUtils.getPath() + "listTemp.xlsx";

    // 方案2 分多次 填充 会使用文件缓存（省内存）
    String fileName = FileUtils.getPath() + "listFill" + System.currentTimeMillis() + ".xlsx";
    try (ExcelWriter excelWriter = EasyExcel.write(fileName).withTemplate(templateFileName).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet().build();
      for (int i = 0; i < 10; i++) {
        excelWriter.fill(MockData.data(), writeSheet);
      }
    }
  }
}
