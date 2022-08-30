package drrename.kodi;

import lombok.*;

@Getter
public class KodiCheckResultElementSubDirs extends KodiCheckResultElement {

    private final SubdirResult subdirs;

    public KodiCheckResultElementSubDirs(String movieName, SubdirResult subdirs) {
        super(movieName);
        this.subdirs = subdirs;
    }



}
