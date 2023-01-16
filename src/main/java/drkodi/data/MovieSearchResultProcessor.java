package drkodi.data;

import drkodi.data.movie.DynamicMovieData;
import drkodi.data.json.MovieSearchResultDto;
import drkodi.data.json.TranslationDto;
import drkodi.data.json.MovieWebSearchResults;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class MovieSearchResultProcessor {
    private final DynamicMovieData movie;
    private final MovieWebSearchResults movieWebSearchResults;

    public void process() {
        movie.getMovieSearchResults().clear();
        if (movieWebSearchResults != null) {
            for (Number id : movieWebSearchResults.getSearchResults().keySet()) {
                MovieSearchResultDto movieSearchResultDto = movieWebSearchResults.getSearchResults().get(id);
                TranslationDto translationDto = movieWebSearchResults.getTranslations().get(id);
                Image image = movieWebSearchResults.getImages().get(id);
                byte[] imageData = movieWebSearchResults.getImageData().get(id);

                SearchResult searchResult = movie.getMovieSearchResultDtoMapper().map(movieSearchResultDto, imageData, image);
               movie.getMovieSearchResults().add(searchResult);

                if (translationDto != null) {
                    SearchResult searchResultTranslated = new SearchResult(searchResult);

                    if(StringUtils.isNotBlank(translationDto.getData().getTitle())) {
                        searchResultTranslated.setTitle(translationDto.getData().getTitle());
                    }
//                    if(StringUtils.isNotBlank(translationDto.getData().getTitle())) {
//                        searchResult.setTitle(translationDto.getData().getTitle());
//                    }
                    if(StringUtils.isNotBlank(translationDto.getData().getOverview())) {
                        searchResultTranslated.setPlot(translationDto.getData().getOverview());
                    }
                    if(StringUtils.isNotBlank(translationDto.getData().getTagline())) {
                        searchResultTranslated.setTagline(translationDto.getData().getTagline());
                    }

                    if(searchResult.getOriginalLanguage() != null && (searchResult.getOriginalLanguage().equalsIgnoreCase(translationDto.getIso639()) || searchResult.getOriginalLanguage().equalsIgnoreCase(translationDto.getIso639()))){
                        // switch title and original title
                        searchResultTranslated.setTitle(searchResult.getOriginalTitle());
//                        searchResultTranslated.setOriginalTitle(searchResult.getTitle());
                    }

                    movie.getMovieSearchResults().add(searchResultTranslated);
//                    SearchResult searchInvertedTitles = new SearchResult(searchResult);
//                    searchInvertedTitles.setTitle(searchResult.getOriginalTitle());
//                    searchInvertedTitles.setOriginalTitle(searchResult.getTitle());
//                    getSearchResults().add(searchInvertedTitles);
                }
            }
        }
    }
}
