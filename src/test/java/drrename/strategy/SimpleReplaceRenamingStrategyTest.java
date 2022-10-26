/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.strategy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleReplaceRenamingStrategyTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSimple(){
        var config = new SimpleRenamingConfig();
        var strat = new SimpleReplaceRenamingStrategy(null, config);
        strat.setReplacementStringFrom("-");
        strat.setReplacementStringTo("_");
        var inputName = "my-file.txt";
        var expectedOutputName = "my_file.txt";
        var newFileName = strat.getNameNew(Paths.get(inputName));
        assertEquals(expectedOutputName, newFileName);
    }

    @Test
    void testTricky01(){
        var config = new SimpleRenamingConfig();
        var strat = new SimpleReplaceRenamingStrategy(null, config);
        strat.setReplacementStringFrom(".");
        strat.setReplacementStringTo("_");
        var inputName = "my.file.txt";
        var expectedOutputName = "my_file.txt";
        var newFileName = strat.getNameNew(Paths.get(inputName));
        assertEquals(expectedOutputName, newFileName);
    }

    @Test
    void testTricky02(){
        var config = new SimpleRenamingConfig();
        config.setIncludeFileExtension(true);
        var strat = new SimpleReplaceRenamingStrategy(null, config);
        strat.setReplacementStringFrom(".");
        strat.setReplacementStringTo("_");
        var inputName = "my.file.txt";
        var expectedOutputName = "my_file_txt";
        var newFileName = strat.getNameNew(Paths.get(inputName));
        assertEquals(expectedOutputName, newFileName);
    }
}