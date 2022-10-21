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

package drrename.kodi.checkservice;

import drrename.kodi.MultipleSpacesService;
import drrename.kodi.ProblematicMovieNameService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Holds all {@link CheckService} instances that should run.
 */
@Service
public class CheckServiceProvider {

    private final List<CheckService<?>> checkServices;

    public CheckServiceProvider(NfoFileContentUrlCheckService nfoFileContentUrlCheckService){

        this.checkServices = Arrays.asList(
                nfoFileContentUrlCheckService,
                new MovieFileNameCheckService(),
                new NfoFileNameCheckService(),
                new NfoFileContentYearCheckService(),
                new NfoFileContentTitleCheckService(),
                new NfoFileContentCoverCheckService(),
                new SubdirsCheckService(),
                new MultipleSpacesService(),
                new ProblematicMovieNameService()
        );
    }

    public List<CheckService<?>> getCheckServices() {
        return checkServices;
    }
}
