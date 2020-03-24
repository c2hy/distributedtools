package suzor.distributedtools.infrastructures.utils;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;

@Component
public class DistributedLock {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);
    private static ThreadLocal<Deque<Pair<String, Lock>>> locks = ThreadLocal.withInitial(LinkedList::new);
    private static LockRegistry staticLockRegistry;
    private final LockRegistry lockRegistry;

    public DistributedLock(LockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

    @PostConstruct
    public void init() {
        staticLockRegistry = this.lockRegistry;
    }

    public static void autoUnlock(Runnable runnable) {
        try {
            logger.debug("使用自动解锁模式");
            runnable.run();
        } finally {
            while (!DistributedLock.locks.get().isEmpty()) {
                Pair<String, Lock> lockPair = DistributedLock.locks.get().pop();
                logger.debug("分布式锁解锁:{}", lockPair.getValue0());
                try {
                    lockPair.getValue1().unlock();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("解锁失败:{}", lockPair.getValue0());
                }
            }
        }
    }

    public static void lock(String lockKey) {
        logger.debug("分布式锁上锁:{}", lockKey);
        Lock lock = staticLockRegistry.obtain(lockKey);
        lock.lock();
        DistributedLock.locks.get().add(new Pair<>(lockKey, lock));
    }
}
