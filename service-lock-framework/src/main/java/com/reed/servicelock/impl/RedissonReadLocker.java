package com.reed.servicelock.impl;

import com.reed.servicelock.ServiceLocker;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 分布式 读锁
 */
@AllArgsConstructor
public class RedissonReadLocker implements ServiceLocker {
    private final RedissonClient redissonClient;

    @Override
    public RLock getLock(String lockKey) {
        return redissonClient.getReadWriteLock(lockKey).readLock();
    }

    @Override
    public RLock lock(String lockKey) {
        RLock rLock = redissonClient.getReadWriteLock(lockKey).readLock();
        rLock.lock();
        return rLock;
    }

    @Override
    public RLock lock(String lockKey, long leaseTime) {
        RLock rLock = redissonClient.getReadWriteLock(lockKey).readLock();
        rLock.lock(leaseTime, TimeUnit.SECONDS);
        return rLock;
    }

    @Override
    public RLock lock(String lockKey, TimeUnit unit, long leaseTime) {
        RLock rLock = redissonClient.getReadWriteLock(lockKey).readLock();
        rLock.lock(leaseTime,unit);
        return rLock;
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime) {
        RLock rLock = redissonClient.getReadWriteLock(lockKey).readLock();
        try {
            return rLock.tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock rLock = redissonClient.getReadWriteLock(lockKey).readLock();
        try {
            return rLock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        RLock rLock = redissonClient.getReadWriteLock(lockKey).readLock();
        rLock.unlock();
    }

    @Override
    public void unlock(RLock lock) {
        lock.unlock();
    }
}
