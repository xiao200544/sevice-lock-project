package com.reed.config;

import com.reed.constant.LockInfoType;
import com.reed.core.ManageLocker;
import com.reed.lockinfo.LockInfoHandle;
import com.reed.lockinfo.handleFactory.LockHandleFactory;
import com.reed.lockinfo.impl.ServiceLockInfoHandle;
import com.reed.servicelock.aspect.ServiceLockAspect;
import com.reed.util.ServiceLockTool;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;

/**
 * 分布式锁 配置
 **/
public class ServiceLockAutoConfiguration {

    @Bean(LockInfoType.SERVICE_LOCK)
    public LockInfoHandle lockHandleFactory(){
        return new ServiceLockInfoHandle();
    }

    @Bean
    public ManageLocker manageLocker(RedissonClient redissonClient){
        return new ManageLocker(redissonClient);
    }

    @Bean
    public ServiceLockAspect serviceLockAspect(LockHandleFactory lockIHandleFactory, ManageLocker manageLocker){
        return new ServiceLockAspect(lockIHandleFactory,manageLocker);
    }

    @Bean
    public ServiceLockTool serviceLockUtil(LockHandleFactory lockIHandleFactory, ManageLocker manageLocker){
        return new ServiceLockTool(lockIHandleFactory,manageLocker);
    }
}
