package com.reed.core;

import com.reed.servicelock.LockType;
import com.reed.servicelock.ServiceLocker;
import com.reed.servicelock.impl.RedissonFairLocker;
import com.reed.servicelock.impl.RedissonReadLocker;
import com.reed.servicelock.impl.RedissonReentrantLocker;
import com.reed.servicelock.impl.RedissonWriteLocker;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;

import static com.reed.servicelock.LockType.*;

public class ManageLocker {

    private final Map<LockType, ServiceLocker> cacheMap = new HashMap<>();

    public ManageLocker(RedissonClient redissonClient) {
        cacheMap.put(Fair, new RedissonFairLocker(redissonClient));
        cacheMap.put(Reentrant, new RedissonReentrantLocker(redissonClient));
        cacheMap.put(Write, new RedissonWriteLocker(redissonClient));
        cacheMap.put(Read, new RedissonReadLocker(redissonClient));
    }

    private ServiceLocker getReentrantLocker(){
        return cacheMap.get(Reentrant);
    }

    private ServiceLocker getFairLocker(){
        return cacheMap.get(Fair);
    }

    private ServiceLocker getWriteLocker(){
        return cacheMap.get(Write);
    }

    private ServiceLocker getReadLocker(){
        return cacheMap.get(Read);
    }

    public ServiceLocker getLock(LockType lockType){
        ServiceLocker lock;
        switch (lockType) {
            case Fair:
                lock = getFairLocker();
                break;
            case Write:
                lock = getWriteLocker();
                break;
            case Read:
                lock = getReadLocker();
                break;
            default:
                lock = getReentrantLocker();
                break;
        }
        return lock;
    }

}
