package drkodi.data;

import drkodi.data.json.TvSearchResultDto;
import drkodi.data.json.TvWebSearchResults;
import drkodi.data.movie.DynamicMovieData;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TvSearchResultProcessor {
    private final DynamicMovieData movie;
    private final TvWebSearchResults movieWebSearchResults;

    public void process() {
        movie.getTvSearchResults().clear();
        if (movieWebSearchResults != null) {
            for (Number id : movieWebSearchResults.getSearchResults().keySet()) {
                TvSearchResultDto movieSearchResultDto = movieWebSearchResults.getSearchResults().get(id);
                Image image = movieWebSearchResults.getImages().get(id);
                byte[] imageData = movieWebSearchResults.getImageData().get(id);

                SearchResult searchResult = movie.getTvSearchResultDtoMapper().map(movieSearchResultDto, imageData, image);
               movie.getTvSearchResults().add(searchResult);
            }
        }
    }
}
