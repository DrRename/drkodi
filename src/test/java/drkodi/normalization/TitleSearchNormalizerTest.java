package drkodi.normalization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TitleSearchNormalizerTest {

    private TitleSearchNormalizer normalizer;

    @BeforeEach
    void setUp() {
        MovieTitleSearchNormalizerConfiguration config = new MovieTitleSearchNormalizerConfiguration();
        config.setDelete(Arrays.asList("AND"));
        normalizer = new TitleSearchNormalizer(config);
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