/*
 *     Dr.Kodi - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2023
 *
 *     This file is part of Dr.Kodi.
 *
 *     You can redistribute it and/or modify it under the terms of the GNU Affero
 *     General Public License as published by the Free Software Foundation, either
 *     version 3 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT
 *     ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drkodi;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NfoElement {

    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Getter
    @Setter
    public static class Art {
        String poster;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Getter
    @Setter
    public static class UniqueId {

        @JacksonXmlText
        Number uniqueid;
        @JacksonXmlProperty(localName = "type", isAttribute = true)
        String uniqueidType;
    }

    Art art;

    String year;

    String premiered;

    String plot;

    @JacksonXmlElementWrapper(useWrapping = false)
    List<String> genre = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    List<String> country = new ArrayList<>();

    String title;

    String originaltitle;

    String tagline;

    UniqueId uniqueid;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "title='" + title + '\'' +
                '}';
    }
}
