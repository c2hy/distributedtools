package suzor.distributedtools;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import suzor.distributedtools.distributedlock.DistributedLockFactory;

@Configuration
@Import({DistributedLockFactory.class})
public class DistributedToolsAutoConfiguration {
}
