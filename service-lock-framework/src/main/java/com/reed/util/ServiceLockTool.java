package com.reed.util;

import com.reed.constant.LockInfoType;
import com.reed.core.ManageLocker;
import com.reed.lockinfo.LockInfoHandle;
import com.reed.lockinfo.handleFactory.LockHandleFactory;
import com.reed.servicelock.LockType;
import com.reed.servicelock.ServiceLocker;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;


@AllArgsConstructor
public class ServiceLockTool {

    private final LockHandleFactory lockHandleFactory;

    private final ManageLocker manageLocker;

    public RLock getLock(LockType lockType, String name, String[] keys){
        LockInfoHandle lockInfoHandle = lockHandleFactory.getLockInfoHandle(LockInfoType.SERVICE_LOCK);
        String lockName = lockInfoHandle.simpleGetLockName(name, keys);
        ServiceLocker lock = manageLocker.getLock(lockType);
        return lock.getLock(lockName);
    }

    public RLock getLock(LockType lockType, String lockName) {
        ServiceLocker lock = manageLocker.getLock(lockType);
        return lock.getLock(lockName);
    }
}
