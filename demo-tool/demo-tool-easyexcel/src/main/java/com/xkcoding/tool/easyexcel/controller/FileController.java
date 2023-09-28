package com.xkcoding.tool.easyexcel.controller;

import com.alibaba.excel.EasyExcel;
import com.xkcoding.tool.easyexcel.dao.EmployeeDao;
import com.xkcoding.tool.easyexcel.data.MockData;
import com.xkcoding.tool.easyexcel.listener.ExcelReadListener;
import com.xkcoding.tool.easyexcel.pojo.Employee;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class FileController {

  // 上传文件, 以 Form 的形式来提交一个 file 字段的 文件
  @PostMapping("upload")
  public Map<String, String> upload(MultipartFile file) throws IOException {
    long t1 = System.currentTimeMillis();
    EasyExcel.read(
      file.getInputStream(),
      Employee.class,
      new ExcelReadListener(new EmployeeDao())
    ).sheet().doRead();
    long t2 = System.currentTimeMillis();
    // 构建返回的结果
    Map<String, String> result = new HashMap<>();
    result.put("code", "0");
    result.put("msg","数据上传成功, 上传时间 : " + (t2 - t1));
    return result;
  }

  // 实现文件的下载 (告诉浏览器已附件的形式下载文件)
  @PostMapping("download")
  public void download(HttpServletResponse response) throws IOException {
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setCharacterEncoding("utf-8");
    // 这里 URLEncoding.encode 可以防止中文乱码, 这是各平台使用的编码的问题，与 easyExcel 没有关系
    String fileName = URLEncoder.encode("员工信息", "UTF-8").replace("\\+", "%20");
    response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    //
    EasyExcel.write(response.getOutputStream(), Employee.class).sheet("用户数据").doWrite(MockData.data());
  }
}
