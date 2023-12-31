package com.xkcoding.event.event;

import org.springframework.context.ApplicationEvent;

public class EmailEvent extends ApplicationEvent {

  private String msg;
  public EmailEvent(Object source, String msg) {
    super(source);
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }
  public void setMsg(String msg) {
    this.msg = msg;
  }
}
