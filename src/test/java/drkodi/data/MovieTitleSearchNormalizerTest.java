package drkodi.data;

import drkodi.normalization.MovieTitleSearchNormalizer;
import drkodi.normalization.MovieTitleSearchNormalizerConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieTitleSearchNormalizerTest {

    private MovieTitleSearchNormalizer normalizer;

    @BeforeEach
    void setUp() {
        MovieTitleSearchNormalizerConfiguration config = new MovieTitleSearchNormalizerConfiguration();
        config.setReplaceWithSpace(List.of("."));
        normalizer = new MovieTitleSearchNormalizer(config);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void normalizeRemoveDots(){
       assertEquals("Hans im Glück", normalizer.normalize("Hans.im.Glück"));
    }
}