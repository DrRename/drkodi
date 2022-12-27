package drkodi.data;

import drkodi.normalization.FolderNameCompareNormalizer;
import drkodi.normalization.FolderNameCompareNormalizerConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FolderNameCompareNormalizerTest {

    private FolderNameCompareNormalizer normalizer;

    @BeforeEach
    void setUp() {
        FolderNameCompareNormalizerConfiguration configuration = new FolderNameCompareNormalizerConfiguration();
        configuration.setDelete(Arrays.asList(",", "'", ":"));
        configuration.setReplaceWithSpace(Arrays.asList("."));
        normalizer = new FolderNameCompareNormalizer(configuration);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void normalize01() {
        assertEquals("Mr Beans Holiday", normalizer.normalize("Mr. Bean's Holiday"));
    }

    @Test
    void normalize02() {
        assertEquals("300 Rise of an Empire", normalizer.normalize("300: Rise of an Empire"));
    }
}