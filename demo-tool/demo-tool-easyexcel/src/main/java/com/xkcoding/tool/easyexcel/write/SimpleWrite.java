package com.xkcoding.tool.easyexcel.write;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.xkcoding.tool.easyexcel.data.MockData;
import com.xkcoding.tool.easyexcel.pojo.Employee;
import com.xkcoding.tool.easyexcel.utils.FileUtils;


/**
 * 注意 simpleWrite在数据量不大的情况下可以使用（5000以内，具体也要看实际情况），数据量大参照 重复多次写入
 */
public class SimpleWrite {
  public static void main(String[] args) {
    write();
  }
  /**
   * 数据写入的方式
   */
  public static void write() {

    // 写法1 JDK8+
    // writeOne();

    // 写法2
    // writeTwo();

    // 写法3
    writeThree();
  }

  // 写法1 JDK8+
  // since: 3.0.0-beta1
  public static void writeOne() {
    String fileName = FileUtils.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
    // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
    // 如果这里想使用03 则 传入excelType参数即可
    EasyExcel.write(fileName, Employee.class)
      .sheet("模板")
      .doWrite(() -> {
        // 分页查询数据
        return MockData.data();
      });
  }

  // 写法 2
  public static void writeTwo() {
    String fileName = FileUtils.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
    // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
    // 如果这里想使用03 则 传入excelType参数即可
    EasyExcel.write(fileName, Employee.class).sheet("模板").doWrite(MockData.data());
  }

  // 写法 3
  // 适合大量数据的分批写入
  public static void writeThree() {
    String fileName = FileUtils.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
    // 这里 需要指定写用哪个class去写
    try (ExcelWriter excelWriter = EasyExcel.write(fileName, Employee.class).build()) {
      // 这里拿到了excelWriter 对象之后，可以使用循环去写入
      for (int i = 0; i < 3; i++) {
        // 这里是循环创建 sheet, 每次的数据都写入到不同的 sheet 页
        // 这里如果需要在同一个表格中写入，则将当前创建 sheet 操作放到循环外面
        WriteSheet writeSheet = EasyExcel.writerSheet("模板" + i).build();
        // 原则上这个取数据的方法可以传入参数然后每次取出的数据不同
        excelWriter.write(MockData.data(), writeSheet);
      }
    }
  }
}
