package com.xkcoding.event.service;

import com.xkcoding.event.publish.EmailEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  @Autowired
  private EmailEventPublisher emailEventPublisher;

  public void order(String goodsId){
    // ...
    // 处理业务逻辑, 发布消息，流程结束
    System.out.println("[" + Thread.currentThread().getName() + "]  发布了消息: " + goodsId + ", 客户邮件:  " + "12138@qq.com");
    emailEventPublisher.sendMsg(goodsId + "12138@qq.com");
  }

}
