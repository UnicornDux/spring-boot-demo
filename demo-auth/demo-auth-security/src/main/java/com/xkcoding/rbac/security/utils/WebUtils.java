package com.xkcoding.rbac.security.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WebUtils {

  public static String renderString(HttpServletResponse response, String data) {
    try {
      response.setStatus(200);
      response.setContentType("application/json");
      response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
      response.getWriter().println(data);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
