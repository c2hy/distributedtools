package suzor.distributedtools.distributedlock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 可重入锁
 */
public class DistributedLock implements AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(DistributedLock.class);
    private final LockRegistry lockRegistry;
    private final Map<String, Deque<Lock>> lockStore = new HashMap<>();

    public DistributedLock(LockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

    /**
     * 不支持多线程调用
     */
    public void lock(String lockKey) {
        this.doLock(lockKey, 0, TimeUnit.MILLISECONDS);
    }

    public void lock(String lockKey, long time, TimeUnit unit) {
        this.doLock(lockKey, time, unit);
    }

    private void doLock(String lockKey, long time, TimeUnit unit) {
        logger.debug("try lock {}", lockKey);
        Lock lock = lockRegistry.obtain(lockKey);
        try {
            if (!lock.tryLock(time, unit)) {
                lock.lock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("lock failed");
        }
        Deque<Lock> deque = lockStore.computeIfAbsent(lockKey, key -> new LinkedList<>());
        deque.push(lock);
        lockStore.put(lockKey, deque);
        logger.debug("locked {}", lockKey);
    }

    @Override
    public void close() {
        this.unlockAll();
    }

    /**
     * 解锁一次
     */
    public void unlock(String lockKey) {
        Deque<Lock> locks = lockStore.get(lockKey);
        locks.pop().unlock();
    }

    /**
     * 解锁指定 key
     */
    public void unlockAll(String lockKey) {
        Deque<Lock> locks = lockStore.get(lockKey);
        this.unlockAll(locks, lockKey);
    }

    /**
     * 解锁所有
     */
    public void unlockAll() {
        Deque<Map.Entry<String, Deque<Lock>>> lockDeque = new ArrayDeque<>(this.lockStore.entrySet());
        while (!lockDeque.isEmpty()) {
            Map.Entry<String, Deque<Lock>> lockPair = lockDeque.pop();
            logger.debug("unlock {}", lockPair.getKey());
            Deque<Lock> locks = lockPair.getValue();
            this.unlockAll(locks, lockPair.getKey());
        }
    }

    private void unlockAll(Deque<Lock> locks, String lockKey) {
        while (!locks.isEmpty()) {
            this.unlock(locks.pop(), lockKey);
        }
    }

    private void unlock(Lock lock, String lockKey) {
        try {
            lock.unlock();
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("unlock fail {}", lockKey);
        }
    }
}
