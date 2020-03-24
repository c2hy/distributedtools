# distributedtools-spring-boot-starter
- DistributedLock

  对于 spring-integration-redis 的封装。使用前先声明 bean

  `org.springframework.integration.support.locks.LockRegistry`

  ```java
  @Configuration
  public class RedisConfig {
      @Bean
      public LockRegistry lockRegistry(RedisConnectionFactory redisConnectionFactory) {
          return new RedisLockRegistry(redisConnectionFactory, "suzor");
      }
  }
  ```

  该类的具体说明可以参考 spring 的文档。

  `DistributedLock.autoUnlock` 方法需要传入一个 Runnable 类型的 lambad 表达式，在 autoUnlock 方法执行的最后会自动解锁。

  `DistributedLock.lock` 方法会声明一个基于 Redis 的分布式锁。

