package suzor.distributedtools.infrastructures.utils;

public class DistributedLockSample {
    public void sample() {
        DistributedLock.autoUnlock(() -> {
            // Business Code...

            // Lock
            DistributedLock.lock("lock key");
        });
        // Unlock
    }
}