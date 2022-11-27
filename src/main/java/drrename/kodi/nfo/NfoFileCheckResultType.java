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

package drrename.kodi.nfo;

import drrename.kodi.WarningsConfig;

public enum NfoFileCheckResultType {
    NO_FILE("No file"){
        public boolean isWarning(WarningsConfig warningsConfig) {
            return warningsConfig.isMissingNfoFileIsWarning();
        }
    },
    DEFAULT_NAME("Default file name"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return warningsConfig.isDefaultNfoFileNameIsWarning();
        }
    },
    MOVIE_NAME("File name matches movie name"),
    INVALID_NAME("Invalid file name"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    },
    INVALID_FILE("Invalid file"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    }
    , INVALID_YEAR("Year does not match"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    }, VALID_YEAR("Valid year"),
    EXCEPTION("Could not parse file"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    },
    MULTIPLE_FILES("Multiple files"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    },

    URL_ONLY_FILE("URL-only file"),
    MISSING_YEAR("No year"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    }, VALID_TITLE("Valid title"),
    INVALID_TITLE("Title does not match folder name"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    }, VALID_POSTER("Valid Poster"),
    INVALID_POSTER("Invalid Poster"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    }, NOT_A_FILE("Not a file"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    }, MISSING_TITLE("Missing title"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    }, MISSING_POSTER("Missing poster"){
        @Override
        public boolean isWarning(WarningsConfig warningsConfig) {
            return true;
        }
    };

    private final String name;

    NfoFileCheckResultType(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isWarning(WarningsConfig warningsConfig) {
        return false;
    }
}
