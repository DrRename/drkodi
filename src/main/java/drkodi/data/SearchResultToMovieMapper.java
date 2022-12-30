package drkodi.data;

import drkodi.data.movie.MovieData;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ImageDataMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SearchResultToMovieMapper {

    @Mapping(target = "movie.movieDbId", source = "searchResult.id")
    @Mapping(target = "movie.movieTitleFromWeb", source = "searchResult.title")
    @Mapping(target = "movie.movieYearFromWeb", source = "searchResult.releaseDate")
    @Mapping(target = "movie.movieOriginalTitle", source = "searchResult.originalTitle")
    void map(@MappingTarget MovieData movie, SearchResult searchResult);


}
