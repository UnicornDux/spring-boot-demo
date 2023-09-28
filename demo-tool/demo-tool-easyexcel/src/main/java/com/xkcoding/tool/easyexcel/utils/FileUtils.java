package com.xkcoding.tool.easyexcel.utils;

public class FileUtils {

  public static String getPath(){
    return FileUtils.class.getResource("/")
              .getPath().replace("classes/", "");
  }



}
