package drrename.kodi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class KodiCheckResultElement<T> {

    protected final KodiCheckResult.Type type;
    protected final String movieName;

    protected final T suggestion;




}
