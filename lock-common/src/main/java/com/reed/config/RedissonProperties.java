package com.reed.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


import java.util.concurrent.TimeUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis.redisson")
public class RedissonProperties {

    private Integer threads = 16;

    private Integer nettyThreads = 32;

    private Integer corePoolSize = null;

    private Integer maximumPoolSize = null;

    private long keepAliveTime = 30;

    private TimeUnit unit = TimeUnit.SECONDS;

    private Integer workQueueSize = 256;
}
