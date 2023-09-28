package com.xkcoding.tool.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.xkcoding.tool.easyexcel.dao.EmployeeDao;
import com.xkcoding.tool.easyexcel.pojo.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的读监听器
 */
public class ExcelReadListener implements ReadListener<Employee> {

  private int count = 100;
  private List<Employee> list = new ArrayList<>();

  private EmployeeDao employeeDao;

  public ExcelReadListener(EmployeeDao employeeDao) {
    this.employeeDao = employeeDao;
  }

  // 每读取一行数据都会调用这个方法
  @Override
  public void invoke(Employee employee, AnalysisContext analysisContext) {
      list.add(employee);
      if (list.size() >= 100) {
        this.employeeDao.save(list);
        list = new ArrayList<>(count);
      }
  }

  // 读取完每个 sheet 后执行这个方法，所有sheet 都是公用同一个监听器，如果有使用到数据容器
  // 一定要在这个方法中清理干净，不然不同的 sheet 之间可能会存在数据污染
  @Override
  public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    if(!list.isEmpty()) {
      this.employeeDao.save(list);
      list = new ArrayList<>();
    }
  }
}
