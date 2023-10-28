package com.xkcoding.event.listener;

import com.xkcoding.event.event.EmailEvent;
import com.xkcoding.event.event.PhoneEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

  // 如果方法执行不依赖于任何参数，可以监听多个事件
  @EventListener(value = { EmailEvent.class, PhoneEvent.class})
  public void emailListener(){
    System.out.println("[" + Thread.currentThread().getName() + "]  annotation listen message");
  }
}
