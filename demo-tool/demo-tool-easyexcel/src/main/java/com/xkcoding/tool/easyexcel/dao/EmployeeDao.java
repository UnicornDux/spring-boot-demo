package com.xkcoding.tool.easyexcel.dao;

import com.xkcoding.tool.easyexcel.pojo.Employee;

import java.util.List;

public class EmployeeDao {
  public void save(List<Employee> employeeList) {
    System.out.println("保存了数据量为 : " + employeeList.size());
  }
}
