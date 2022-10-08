package com.github.drrename.strategy;

import drrename.strategy.RenamingStrategyProto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;

class RenamingStrategyProtoTest {

    private RenamingStrategyProto strat;

    ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH);

    @BeforeEach
    void setUp() {
        strat = new RenamingStrategyProto(bundle) {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getNameNew(Path file) {
                return null;
            }

            @Override
            protected String getNameId() {
                return null;
            }
            @Override
            protected String getHelpTextId() {
                return null;
            }
            @Override
            public boolean isReplacing() {
                return false;
            }
        };
    }

    @AfterEach
    void tearDown() {
        strat = null;
    }

    @Test
    void getFileAlreadyExistsFileName01() throws IOException, InterruptedException {
        String nameNew = strat.getFileAlreadyExistsFileName("new.jpg", 0);
        assertThat(nameNew).isEqualTo("new_copy1.jpg");
    }

    @Test
    void getFileAlreadyExistsFileName02() throws IOException, InterruptedException {
        String nameNew = strat.getFileAlreadyExistsFileName("new.jpg", 1);
        assertThat(nameNew).isEqualTo("new_copy2.jpg");
    }
}