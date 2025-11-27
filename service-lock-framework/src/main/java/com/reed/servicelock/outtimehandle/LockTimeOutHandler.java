package com.reed.servicelock.outtimehandle;

/**
分布式锁 处理失败抽象
 **/
public interface LockTimeOutHandler {
    
    /**
     * 处理
     * @param lockName 锁名
     * */
    void handler(String lockName);
}
