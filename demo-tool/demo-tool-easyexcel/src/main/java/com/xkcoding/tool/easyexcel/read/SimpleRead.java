package com.xkcoding.tool.easyexcel.read;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xkcoding.tool.easyexcel.pojo.Employee;
import com.xkcoding.tool.easyexcel.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SimpleRead {
  public static void main(String[] args) {
    read();
  }

  public static void read() {
    // 方法 1
    readOne();
  }


  public static void  readOne() {
    // 写法1：JDK8+ ,不用额外写一个DemoDataListener
    // since: 3.0.0-beta1
    String fileName = FileUtils.getPath() + "simpleWrite1694875324427.xlsx";
    // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
    // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
    EasyExcel.read(fileName, Employee.class, new PageReadListener<Employee>(dataList -> {
      for (Employee employee : dataList) {
        try {
          log.info("读取到一条数据{}", new ObjectMapper().writeValueAsString(employee));
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
    })).sheet().doRead();
  }

}
