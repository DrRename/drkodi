package drkodi.normalization;

import drkodi.ProtoTypeNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MovieTitleSearchNormalizer extends ProtoTypeNormalizer {

    private final MovieTitleSearchNormalizerConfiguration configuration;

    @Override
    protected ProtoTypeNormalizerConfiguration getConfiguration() {
        return configuration;
    }

    public String normalize(String movieTitle){
        return super.normalize(movieTitle);
    }


}
