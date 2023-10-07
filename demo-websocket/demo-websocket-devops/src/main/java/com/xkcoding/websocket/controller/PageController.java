package com.xkcoding.websocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/api/socket/")
public class PageController {

  // 页面请求
  @GetMapping("/index/{userId}")
  public ModelAndView socket(@PathVariable("userId") String userId) {
    ModelAndView mv = new ModelAndView("index");
    System.out.println(userId);
    mv.addObject("userId", userId);
    return mv;
  }
}
