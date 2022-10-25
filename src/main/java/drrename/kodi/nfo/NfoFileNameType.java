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

public enum NfoFileNameType {
    NO_FILE("No file"), MOVIE_NAME("File name matching movie name"), DEFAULT_NAME("Default file name"), MULTIPLE_FILES("Multiple files"), INVALID_NAME("Invalid file name"), ERROR("Error");

    private final String name;

    NfoFileNameType(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
