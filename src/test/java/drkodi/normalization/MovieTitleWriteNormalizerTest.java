package drkodi.normalization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MovieTitleWriteNormalizerTest {

    private MovieTitleWriteNormalizer normalizer;

    @BeforeEach
    void setUp() {
        MovieTitleWriteNormalizerConfiguration config = new MovieTitleWriteNormalizerConfiguration();
        config.setDelete(Arrays.asList(",", ":", "'"));
        config.setReplaceWithSpace(Arrays.asList("."));
        normalizer = new MovieTitleWriteNormalizer(config);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testDelete01() {
        String result = normalizer.normalize("hans, dampf");
        assertEquals("hans dampf", result);
    }

    @Test
    void testDelete02() {
        String result = normalizer.normalize("hans: dampf");
        assertEquals("hans dampf", result);
    }

    @Test
    void testDelete03() {
        String result = normalizer.normalize("hans' dampf");
        assertEquals("hans dampf", result);
    }

    @Test
    void testReplaceWithSpace01() {
        String result = normalizer.normalize("hans.dampf");
        assertEquals("hans dampf", result);
    }
}