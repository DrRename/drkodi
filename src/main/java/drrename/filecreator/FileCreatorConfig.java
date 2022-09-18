package drrename.filecreator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.filecreator")
public class FileCreatorConfig {

    private boolean debug;

    private long loopDelayMs;

    private long wordCnt;
}
