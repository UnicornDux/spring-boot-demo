package com.xkcoding.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 这里主要探讨 ApplicationEvent, ApplicationListener 的应用
 */

@SpringBootApplication
public class EventApplication {
  public static void main(String[] args) {
    SpringApplication.run(EventApplication.class, args);
  }
}
