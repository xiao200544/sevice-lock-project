package com.reed.servicelock.aspect;

import com.reed.constant.LockInfoType;
import com.reed.core.ManageLocker;
import com.reed.lockinfo.LockInfoHandle;
import com.reed.lockinfo.handleFactory.LockHandleFactory;
import com.reed.servicelock.LockType;
import com.reed.servicelock.ServiceLocker;
import com.reed.servicelock.annotion.ServiceLock;
import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Order(-1)
@AllArgsConstructor
public class ServiceLockAspect {

    private final LockHandleFactory lockHandleFactory;

    private final ManageLocker  manageLocker;

    @Around("@annotation(serviceLock)")
    public Object around(ProceedingJoinPoint joinPoint, ServiceLock serviceLock) throws Throwable {
        // 获取分布式锁的解析器，点进去会发现是根据名字和类型找bean,所以如果有多个解析器，一定要给解析器名字
        LockInfoHandle lockInfoHandle = lockHandleFactory.getLockInfoHandle(LockInfoType.SERVICE_LOCK);
        // 根据解析器得到锁的名字
        String lockName = lockInfoHandle.getLockName(joinPoint, serviceLock.name(), serviceLock.keys());
        LockType lockType = serviceLock.lockType();
        long waitTime = serviceLock.waitTime();
        TimeUnit timeUnit = serviceLock.timeUnit();

        ServiceLocker lock = manageLocker.getLock(lockType);
        boolean success = lock.tryLock(lockName, timeUnit, waitTime);
        if (success) {
            try {
                return joinPoint.proceed();
            }
            finally {
                lock.unlock(lockName);
            }
        } else {
            log.warn("Timeout while acquiring serviceLock:{}",lockName);
            // 判断有没有自定义的超时策略
            String customLockTimeoutStrategy = serviceLock.customLockTimeoutStrategy();
            if (StringUtil.isNotEmpty(customLockTimeoutStrategy)) {
                return handleCustomLockTimeoutStrategy(customLockTimeoutStrategy, joinPoint);
            }else{
                // 没有自定义的策略，使用非自定义的
                serviceLock.lockTimeoutStrategy().handler(lockName);
            }
            return joinPoint.proceed();
        }

    }

    public Object handleCustomLockTimeoutStrategy(String customLockTimeoutStrategy, JoinPoint joinPoint) {
        // prepare invocation context
        // 拿到当前加锁的方法
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 拿到被代理的对象
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            // 拿到自定义的策略方法
            handleMethod = target.getClass().getDeclaredMethod(customLockTimeoutStrategy, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Illegal annotation param customLockTimeoutStrategy :" + customLockTimeoutStrategy,e);
        }
        // 拿到方法传入的真实参数的值
        Object[] args = joinPoint.getArgs();

        // invoke
        Object result;
        try {
            // 调用策略方法
            result = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Fail to illegal access custom lock timeout handler: " + customLockTimeoutStrategy ,e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Fail to invoke custom lock timeout handler: " + customLockTimeoutStrategy ,e);
        }
        return result;
    }

}
