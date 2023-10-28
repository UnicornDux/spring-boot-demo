package com.xkcoding.event.controller;

import com.xkcoding.event.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @GetMapping("/{order}")
  public String queryOrder(@PathVariable("order") String order) {
    // 方法调用
    orderService.order(order + "-" + System.currentTimeMillis());
    return "ok";
  }
}
