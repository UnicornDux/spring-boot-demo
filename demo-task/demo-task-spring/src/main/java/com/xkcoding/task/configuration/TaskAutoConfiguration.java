package com.xkcoding.task.configuration;

import cn.hutool.core.thread.NamedThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * <p>
 * 定时任务配置，配置线程池，使用不同线程执行任务，提升效率
 * ------------------------------------------------------------------
 *  > 定时任务的线程池配置，默认 Spring 定时任务为单线程执行
 *  > 配置定时任务，Spring 为我们提供了多种方式，
 *     - 可以在程序中构建 ScheduleExecutorService Bean 对象
 *     - 实现 SchedulingConfigurer 接口, 在 configureTasks 配置方法中注入线程池
 *     - 在配置文件中配置线程池参数
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-22 19:02
 */

// @EnableScheduling
// @Configuration
public class TaskAutoConfiguration implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    /**
     * 这里等同于配置文件配置
     * {@code spring.task.scheduling.pool.size=20} - Maximum allowed number of threads.
     * {@code spring.task.scheduling.thread-name-prefix=Job-Thread- } - Prefix to use for the names of newly created threads.
     * {@link org.springframework.boot.autoconfigure.task.TaskSchedulingProperties}
     */
    @Bean
    public Executor taskExecutor() {
        return new ScheduledThreadPoolExecutor(20, new NamedThreadFactory("Job-Thread-", false));
    }
}
