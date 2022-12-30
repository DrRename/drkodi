package drkodi.data;

import drkodi.data.movie.DynamicMovieData;
import drkodi.data.json.SearchResultDto;
import drkodi.data.json.TranslationDto;
import drkodi.data.json.WebSearchResults;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class MovieDbSearchResultProcessor {
    private final DynamicMovieData movie;
    private final WebSearchResults webSearchResults;

    public void process() {
        if (webSearchResults != null) {
            for (Number id : webSearchResults.getSearchResults().keySet()) {
                SearchResultDto searchResultDto = webSearchResults.getSearchResults().get(id);
                TranslationDto translationDto = webSearchResults.getTranslations().get(id);
                Image image = webSearchResults.getImages().get(id);
                byte[] imageData = webSearchResults.getImageData().get(id);

                SearchResult searchResult = movie.getSearchResultDtoMapper().map(searchResultDto, imageData, image);
               movie.getSearchResults().add(searchResult);

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

                    movie.getSearchResults().add(searchResultTranslated);
//                    SearchResult searchInvertedTitles = new SearchResult(searchResult);
//                    searchInvertedTitles.setTitle(searchResult.getOriginalTitle());
//                    searchInvertedTitles.setOriginalTitle(searchResult.getTitle());
//                    getSearchResults().add(searchInvertedTitles);
                }
            }
        } else {
            movie.getSearchResults().clear();
        }
    }
}
