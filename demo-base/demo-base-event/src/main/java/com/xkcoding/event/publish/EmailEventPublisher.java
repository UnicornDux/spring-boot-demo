package com.xkcoding.event.publish;

import com.xkcoding.event.event.EmailEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class EmailEventPublisher implements ApplicationEventPublisherAware {
  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void sendMsg(String msg) {
    applicationEventPublisher.publishEvent(new EmailEvent(this, msg));
  }
}
