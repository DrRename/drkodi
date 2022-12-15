package drrename.kodi.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieTitleSearchNormalizerTest {

    private MovieTitleSearchNormalizer normalizer;

    @BeforeEach
    void setUp() {
        SearchNormalizeConfiguration config = new SearchNormalizeConfiguration();
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