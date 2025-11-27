package com.reed.servicelock.annotion;

import com.reed.servicelock.LockType;
import com.reed.servicelock.outtimehandle.LockTimeOutStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(value= {ElementType.TYPE, ElementType.METHOD})
@Retention(value= RetentionPolicy.RUNTIME)
public @interface ServiceLock {

    /**
     * 锁的类型(读锁，写锁...),默认是可重入锁
     */
    LockType lockType() default LockType.Reentrant;

    /**
     * 业务名称
     * @return
     */
    String name() default "";

    /**
     * 自定义的业务参数
     * @return
     */
    String[] keys();

    /**
     * 尝试加锁失败的最大等待时间
     * @return
     */
    long waitTime() default 10;

    /**
     * 等待时间的单位
     * @return
     */
    TimeUnit  timeUnit() default TimeUnit.SECONDS;

    /**
     * 加锁超时的处理策略
     * @return LockTimeOutStrategy
     */
    LockTimeOutStrategy lockTimeoutStrategy() default LockTimeOutStrategy.FAIL;

    // 出现加锁超时后会先去找 自定义的策略没有就使用非自定义的策略
    // 自定义的策略是一个方法的名字，一般会在枷锁的类里找这个名字的方法调用
    /**
     * 自定义加锁超时的处理策略,一般会在枷锁的类里找这个名字的方法调用
     * @return customLockTimeoutStrategy
     */
    String customLockTimeoutStrategy() default "";
}
