package drrename.kodi;

import lombok.RequiredArgsConstructor;

import java.util.List;


public abstract class ProtoTypeNormalizer {

    public interface ProtoTypeNormalizerConfiguration {

        List<String> getReplaceWithSpace();

        List<String> getDelete();
    }

    protected abstract ProtoTypeNormalizerConfiguration getConfiguration();

    public String normalize(String movieTitle){
        String result = movieTitle;
        for(String ex : getConfiguration().getReplaceWithSpace()){
            result = result.replace(ex, " ");
        }
        for(String ex : getConfiguration().getDelete()){
            result = result.replace(ex, "");
        }
        result = result.replaceAll("\\s{2,}", " ");
        return result;
    }


}
