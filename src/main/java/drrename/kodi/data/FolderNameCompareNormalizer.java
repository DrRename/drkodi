package drrename.kodi.data;

import drrename.kodi.ProtoTypeNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FolderNameCompareNormalizer extends ProtoTypeNormalizer {

    private final FolderNameCompareNormalizeConfiguration folderNameCompareNormalizeConfiguration;

    @Override
    protected ProtoTypeNormalizerConfiguration getConfiguration() {
        return folderNameCompareNormalizeConfiguration;
    }

    public String normalize(String movieTitle){
        return super.normalize(movieTitle);
    }


}
