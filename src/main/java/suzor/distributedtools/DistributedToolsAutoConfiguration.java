package suzor.distributedtools;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import suzor.distributedtools.infrastructures.utils.DistributedLock;

@Configuration
@ConditionalOnWebApplication
@Import({DistributedLock.class})
public class DistributedToolsAutoConfiguration {
}
