package com.xkcoding.event.event;

import org.springframework.context.ApplicationEvent;

public class PhoneEvent extends ApplicationEvent {
  public PhoneEvent(Object source) {
    super(source);
  }
}
