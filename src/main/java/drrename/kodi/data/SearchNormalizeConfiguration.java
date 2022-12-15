package drrename.kodi.data;

import drrename.kodi.ProtoTypeNormalizer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.search.normalize")
public class SearchNormalizeConfiguration implements ProtoTypeNormalizer.ProtoTypeNormalizerConfiguration {

    private List<String> delete = new ArrayList<>();

    private List<String> replaceWithSpace = new ArrayList<>();
}
