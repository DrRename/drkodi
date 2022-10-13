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

package drrename;

import drrename.ui.config.UiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.ResourceBundle;

@Component
@Slf4j
public class Beans {

    private final UiConfig config;

    public Beans(UiConfig config) {
        this.config = config;
    }

    @Bean
    public ResourceBundle bundle() {
        Locale locale = Locale.getDefault();
        if(config.getOverrideLocale() != null) {
            locale = Locale.forLanguageTag(config.getOverrideLocale());
        }
        log.debug("Locale: {}", locale);
        return ResourceBundle.getBundle("i18n/messages", locale);
    }
}
