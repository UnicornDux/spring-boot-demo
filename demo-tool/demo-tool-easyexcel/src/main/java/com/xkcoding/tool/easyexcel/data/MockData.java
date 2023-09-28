package com.xkcoding.tool.easyexcel.data;

import com.alibaba.excel.util.ListUtils;
import com.xkcoding.tool.easyexcel.pojo.Employee;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockData {
  public static List<Employee> data() {
    ArrayList<Employee> employees = ListUtils.newArrayList();
    for (int i = 0; i < 2000; i++) {
      employees.add(new Employee( i + 100000, "Alex", new Date(), "男", (float) (6.6 * i), "ignore"));
    }
    return employees;
  }
}
