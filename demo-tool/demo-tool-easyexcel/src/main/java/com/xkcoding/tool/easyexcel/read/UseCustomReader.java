package com.xkcoding.tool.easyexcel.read;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.xkcoding.tool.easyexcel.dao.EmployeeDao;
import com.xkcoding.tool.easyexcel.listener.ExcelReadListener;
import com.xkcoding.tool.easyexcel.pojo.Employee;
import com.xkcoding.tool.easyexcel.utils.FileUtils;

public class UseCustomReader {
  public static void main(String[] args) {
    readUseCustomReader();
  }

  public static void  readUseCustomReader() {
    // 写法1：JDK8+ ,不用额外写一个DemoDataListener
    // since: 3.0.0-beta1
    String fileName = FileUtils.getPath() + "simpleWrite1694878335715.xlsx";
    // 获取到一个读取数据的对象
    ExcelReader reader = EasyExcel.read(fileName, Employee.class, new ExcelReadListener(new EmployeeDao())).build();
    // 告诉read 需要读取哪个 sheet, 没有传入的时候默认读取第一个
    // ReadSheet sheet = EasyExcel.readSheet().build();
    for (int i = 0; i < 3; i++) {
      ReadSheet sheet = EasyExcel.readSheet(i).build();
      reader.read(sheet);
    }
  }
}
