package drrename.kodi;

import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class KodiCheckResult {

    private final Map<String, KodiCheckResultElement> elements = new TreeMap<>();

    public KodiCheckResult addResult(KodiCheckResultElement kodiCheckResultElement){
        this.elements.put(kodiCheckResultElement.movieName, kodiCheckResultElement);
        return this;
    }
}
