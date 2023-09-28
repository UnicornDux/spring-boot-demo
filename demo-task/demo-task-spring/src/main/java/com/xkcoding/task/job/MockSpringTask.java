package com.xkcoding.task.job;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 定时任务 : 关于定时任务的几点注意事项
 * ------------------------------------------------------------------------
 * > 定时任务设定的时间参数都是指定的任务生成的时间间隔或者时间延迟，生成出来的任务被放入到
 *   任务的队列中，工作线程会从任务队列的队首查看任务是否到达时间可以执行，
 * > 只有前面的任务执行完成了，后面的任务才会检测是否已经到达执行的时间，如果任务执行时间超过
 *   任务生成的时间，则队列中会积压任务，观测到的执行时间间隔可能就不是我们设置的时间间隔
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-22 19:09
 */
@Slf4j
@Component
@EnableScheduling
public class MockSpringTask {

    /**
     * 按照标准时间来算，每隔 10s 执行一次
     * cron 这里只有 6 位, 不是完整的 cron 表达式，没有最后以为 年
     * -------------------------------------------------------------
     * > 当在某些分布式场景的时候，某些机器需要运行，某些机器不运行，
     * > 可以将运行的时间配置到配置文件中, 不需要运行的可以使用 "-",
     * (不能配置无法解析的表达式, 程序无法启动, 只能使用 "-" 代替)
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void job1() {
        log.info("【job1】开始执行：{}", DateUtil.formatDateTime(new Date()));
    }

    /**
     * 从启动时间开始，间隔 2s 执行
     * 固定间隔时间, 按照一定的时间间隔构建任务, 时间以程序启动时间开始计算
     * 假设以 2s 的间隔，则构建任务的时间为 2, 4, 6, 8.... (具体程序输入以运行时间为准)
     *  > 如果程序执行所需的时间在任务生成的间隔之内，则可以看到任务就是按照设定的时间间隔运行
     *  > 如果程序执行所需的时间在超过了任务生成间隔，则任务阻塞，看到任务执行是按照任务执行时间间隔
     */
    @Scheduled(fixedRate = 2000)
    public void job2() throws InterruptedException {
        log.info("【job2】开始执行：{}", DateUtil.formatDateTime(new Date()));
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * 从启动时间开始，初次延迟 5s 后间隔 4s 执行
     * 固定等待时间, 以上一个任务结束开始记时等待(4s) 之后构建任务.
     * > 任务执行时间(输出) 以任务生成间隔和任务执行时间相加
     */
    @Scheduled(fixedDelay = 4000, initialDelay = 5000)
    public void job3() throws InterruptedException {
        log.info("【job3】开始执行：{}", DateUtil.formatDateTime(new Date()));
        TimeUnit.MILLISECONDS.sleep(2000);
    }
}
