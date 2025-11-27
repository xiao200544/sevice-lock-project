package com.reed.servicelock.impl;

import com.reed.servicelock.ServiceLocker;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 分布式 公平锁
 */
@AllArgsConstructor
public class RedissonFairLocker implements ServiceLocker {

    private final RedissonClient redissonClient;

    @Override
    public RLock getLock(String lockKey) {
        return redissonClient.getFairLock(lockKey);
    }

    @Override
    public RLock lock(String lockKey) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        fairLock.lock();
        return fairLock;
    }

    @Override
    public RLock lock(String lockKey, long leaseTime) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        fairLock.lock(leaseTime, TimeUnit.SECONDS);
        return fairLock;
    }

    @Override
    public RLock lock(String lockKey, TimeUnit unit, long leaseTime) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        fairLock.lock(leaseTime, unit);
        return fairLock;
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        try {
            return fairLock.tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        try {
            return fairLock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        redissonClient.getFairLock(lockKey).unlock();
    }

    @Override
    public void unlock(RLock lock) {
        lock.unlock();
    }
}
