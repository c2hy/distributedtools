# distributedtools-spring-boot-starter
- DistributedLock

  对于 spring-integration-redis 的封装。该锁是可重入锁，但不支持在多线程的环境下共享锁，会导致解锁失败。

  使用前先声明 bean `org.springframework.integration.support.locks.LockRegistry`

  ```java
  @Configuration
  public class RedisConfig {
      @Bean
      public LockRegistry lockRegistry(RedisConnectionFactory redisConnectionFactory) {
          return new RedisLockRegistry(redisConnectionFactory, "key");
      }
  }
  ```

  该类的具体说明可以参考 spring 的文档。

  `DistributedLockFactory.create` 创建一个锁。

  `DistributedLock` 使用 try with resource 机制自动解锁，避免因为异常问题而死锁。

  ```java
  public void sample() {
      try(DistributedLock distributedLock = DistributedLockFactory.create()){
          // 执行过程中手动解除部分锁
          distributedLock.lock("key");
          distributedLock.unlock("key");
      }
      // 执行结束自动解锁
  }
  ```

  `DistributedLock.lock` 方法会声明一个基于 Redis 的分布式锁，锁可重入，可以通过传入时间来声明获取锁超时的时间，如果超时会抛出异常。

  `DistributedLock.unlock` 方法可以进行解锁。根据传入的参数，可以只解锁一次或解锁一个key的重入锁的全部锁，或解锁全部的锁。

