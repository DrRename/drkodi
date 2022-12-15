package drrename.kodi.data;

import drrename.kodi.ProtoTypeNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MovieTitleSearchNormalizer extends ProtoTypeNormalizer {

    private final SearchNormalizeConfiguration searchNormalizeConfiguration;

    @Override
    protected ProtoTypeNormalizerConfiguration getConfiguration() {
        return searchNormalizeConfiguration;
    }

    public String normalize(String movieTitle){
        return super.normalize(movieTitle);
    }


}
