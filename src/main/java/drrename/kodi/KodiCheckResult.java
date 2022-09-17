package drrename.kodi;

import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class KodiCheckResult {

    enum Type {
        NFO_FILE_NAME, NFO_CONTENT, SUB_DIRS
    }

    private final Map<Type, Map<String, KodiCheckResultElement>> elements = new TreeMap<>();

    public KodiCheckResult addResult(KodiCheckResultElement kodiCheckResultElement){
        var hans = elements.get(kodiCheckResultElement.type);
        if(hans == null){
            hans = new TreeMap<>();
        }
        hans.put(kodiCheckResultElement.movieName, kodiCheckResultElement);
        elements.put(kodiCheckResultElement.type, hans);
        return this;
    }
}
