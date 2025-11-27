package com.reed.lockinfo.impl;

import com.reed.lockinfo.AbsLockInfoHandle;

public class ServiceLockInfoHandle extends AbsLockInfoHandle {

    private static final String LOCK_PREFIX_NAME = "SERVICE_LOCK";

    @Override
    protected String getLockPrefixName() {
        return LOCK_PREFIX_NAME;
    }
}
