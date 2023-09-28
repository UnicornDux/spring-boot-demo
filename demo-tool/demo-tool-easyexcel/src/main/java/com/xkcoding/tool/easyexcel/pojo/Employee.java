package com.xkcoding.tool.easyexcel.pojo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

  @ExcelProperty("员工编号")
  private int id;
  @ExcelProperty("员工姓名")
  private String name;
  @ExcelProperty("生日")
  private Date birthday;
  @ExcelProperty("性别")
  private String gender;
  @ExcelProperty("薪资")
  private Float salary;

  // 忽略这个字段
  @ExcelIgnore
  private String ignore;

}
