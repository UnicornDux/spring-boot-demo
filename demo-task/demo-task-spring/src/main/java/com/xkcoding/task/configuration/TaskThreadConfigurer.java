package com.xkcoding.task.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class TaskThreadConfigurer {

  // 实证返回 ThreadPoolExecutor Bean 和 ExecutorService Bean 都不会自动用作定时任务的线程池
  // 即使内部的实现类构建的是 ScheduledThreadPoolExecutor 也不会自动识别
  @Bean
  public ScheduledExecutorService taskPool(){
    return new ScheduledThreadPoolExecutor(
      20,
      getThreadFactory()
    );
  }

  private ThreadFactory getThreadFactory() {
    return new ThreadFactory(){
      AtomicInteger at = new AtomicInteger(10);
      @Override
      public Thread newThread(Runnable r) {
        return new Thread(r, "pool-thread-" + at.getAndIncrement());
      }
    };
  }
}
