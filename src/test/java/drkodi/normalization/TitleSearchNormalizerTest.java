package drkodi.normalization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TitleSearchNormalizerTest {

    TitleSearchNormalizer normalizer;

    MovieTitleSearchNormalizerConfiguration config;

    @BeforeEach
    void setUp() {
        config = new MovieTitleSearchNormalizerConfiguration();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testAndDelete01() {
        config.setDelete(Arrays.asList("AND"));
        normalizer = new TitleSearchNormalizer(config);
        String result = normalizer.normalize("Einfach Anders");
        assertEquals("Einfach Anders", result);
    }

    @Test
    void testAndDelete02() {
        config.setDelete(Arrays.asList("AND"));
        normalizer = new TitleSearchNormalizer(config);
        String result = normalizer.normalize("Einfach and Anders");
        assertEquals("Einfach Anders", result);
    }

    @Test
    void testUnderscoreDelete01() {
        config.setReplaceWithSpace(Arrays.asList("_"));
        normalizer = new TitleSearchNormalizer(config);
        String result = normalizer.normalize("Hans_Dampf");
        assertEquals("Hans Dampf", result);
    }


}