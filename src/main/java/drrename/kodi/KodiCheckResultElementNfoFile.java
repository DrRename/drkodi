package drrename.kodi;


import lombok.*;

@Getter
public class KodiCheckResultElementNfoFile extends KodiCheckResultElement{

    public KodiCheckResultElementNfoFile(String movieName, NfoFile nfoFile) {
        super(movieName);
        this.nfoFile = nfoFile;
    }

    public enum NfoFile {NO_FILE, MOVIE_NAME, DEFAULT_NAME, INVALID_NAME}

    private NfoFile nfoFile;

    @Override
    public String toString() {
        return nfoFile.toString();
    }
}
