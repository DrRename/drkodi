package drrename.ui.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.ui")
public class UiConfig {

    private String appTitle;

    private int initialWidth;

    private int initialHeight;

    private String overrideLocale;
}
