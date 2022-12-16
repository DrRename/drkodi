package drkodi.normalization;

import drkodi.ProtoTypeNormalizer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.normalize.title.write")
public class MovieTitleWriteNormalizerConfiguration implements ProtoTypeNormalizer.ProtoTypeNormalizerConfiguration {

    private List<String> delete = new ArrayList<>();

    private List<String> replaceWithSpace = new ArrayList<>();
}
