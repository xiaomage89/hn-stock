package com.hn.market.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
@Slf4j
public class ExecutorConfig {
    @Value("${spring.task.execution.pool.core-size}")
    private int corePoolSize;
    @Value("${spring.task.execution.pool.max-size}")
    private int maxPoolSize;
    @Value("${spring.task.execution.pool.queue-capacity}")
    private int queueCapacity;
    @Value("${spring.task.execution.thread-name-prefix}")
    private String namePrefix;
    @Value("${spring.task.execution.pool.keep-alive}")
    private int keepAliveSeconds;
    @Bean(name = "myAsync")
    public Executor myAsync() {
        // 当池子大小小于corePoolSize，就新建线程，并处理请求
        // 当池子大小等于corePoolSize，把请求放入workQueue(QueueCapacity)中，池子里的空闲线程就去workQueue中取任务并处理
        // 当workQueue放不下任务时，就新建线程入池，并处理请求，如果池子大小撑到了maximumPoolSize，就用RejectedExecutionHandler来做拒绝处理
        // 当池子的线程数大于corePoolSize时，多余的线程会等待keepAliveTime长时间，如果无请求可处理就自行销毁
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //核心线程数
        executor.setCorePoolSize(corePoolSize);
        //任务队列的大小
        executor.setQueueCapacity(queueCapacity);
        //线程前缀名
        executor.setThreadNamePrefix(namePrefix);
        //线程活跃时间（秒）
        executor.setKeepAliveSeconds(keepAliveSeconds);
        //设置线程池关闭的时候是否等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //该方法用来设置线程池中任务的等待时间,如果超过这个时候还没有销毁就强制销毁
        // 以确保应用最后能够被关闭，而不是阻塞住。
        executor.setAwaitTerminationSeconds(60);
        //设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 执行初始化
        executor.initialize();
        return executor;
    }
}
