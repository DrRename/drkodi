package drrename.kodi.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FolderNameCompareNormalizerTest {

    private FolderNameCompareNormalizer normalizer;

    @BeforeEach
    void setUp() {
        FolderNameCompareNormalizeConfiguration configuration = new FolderNameCompareNormalizeConfiguration();
        configuration.setDelete(Arrays.asList(",", "'"));
        configuration.setReplaceWithSpace(Arrays.asList("."));
        normalizer = new FolderNameCompareNormalizer(configuration);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void normalize() {
        assertEquals("Mr Beans Holiday", normalizer.normalize("Mr. Bean's Holiday"));
    }
}