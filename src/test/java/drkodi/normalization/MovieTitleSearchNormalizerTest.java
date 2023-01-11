package drkodi.normalization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieTitleSearchNormalizerTest {

    private MovieTitleSearchNormalizer normalizer;

    @BeforeEach
    void setUp() {
        MovieTitleSearchNormalizerConfiguration config = new MovieTitleSearchNormalizerConfiguration();
        config.setDelete(Arrays.asList("and"));
        normalizer = new MovieTitleSearchNormalizer(config);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testDelete01() {
        String result = normalizer.normalize("Einfach Anders");
        assertEquals("Einfach Anders", result);
    }

    @Test
    void testDelete02() {
        String result = normalizer.normalize("Einfach and Anders");
        assertEquals("Einfach Anders", result);
    }


}