package com.xkcoding.websocket.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AppInfo {
  private String application;
  private String environment;
  private String operation;
}
