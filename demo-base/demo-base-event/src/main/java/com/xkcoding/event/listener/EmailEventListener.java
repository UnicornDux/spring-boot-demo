package com.xkcoding.event.listener;

import com.xkcoding.event.event.EmailEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EmailEventListener implements ApplicationListener<EmailEvent> {
  @Override
  public void onApplicationEvent(EmailEvent event) {
    String msg = event.getMsg();
    System.out.println("["+ Thread.currentThread().getName() + "]  bean-listener receive publisher message: " + msg);
  }
}
