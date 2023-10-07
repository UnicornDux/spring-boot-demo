package com.xkcoding.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 *  Spring 为了兼容 JDK 中定义的 websocket 规范，提供了简单的配置
 *  轻松实现 websocket 端点的注册
 */
@Configuration
public class WebSocketConfig {
  @Bean
  public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }
}
