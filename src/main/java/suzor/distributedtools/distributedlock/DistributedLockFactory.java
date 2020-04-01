package suzor.distributedtools.distributedlock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

@Component
public class DistributedLockFactory {
    private static LockRegistry lockRegistry;

    public static DistributedLock create() {
        return new DistributedLock(lockRegistry);
    }

    @Autowired
    private void setLockRegistry(LockRegistry lockRegistry) {
        DistributedLockFactory.lockRegistry = lockRegistry;
    }
}
