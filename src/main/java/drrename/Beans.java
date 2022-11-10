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

import drrename.ui.ResourceBundleAwareLazyFxControllerAndViewResolver;
import drrename.ui.config.UiConfig;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
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
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        // Would also work with javafx-weaver-core only:
        // return new FxWeaver(applicationContext::getBean, applicationContext::close);
        return new SpringFxWeaver(applicationContext);
    }

    @Bean
    public Settings settings(SettingsProvider settingsProvider){
        var settings = settingsProvider.load();
        log.debug("Instantiated settings {}", settings);
        return settings;
    }

    /**
     * See {@link net.rgielen.fxweaver.samples.springboot.controller.DialogController#DialogController(FxControllerAndView)}
     * for an example usage.
     * <p/>
     * <strong>MUST be in scope prototype!</strong>
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public <C, V extends Node> FxControllerAndView<C, V> controllerAndView(FxWeaver fxWeaver,
                                                                           InjectionPoint injectionPoint, ResourceBundle bundle) {
        return new ResourceBundleAwareLazyFxControllerAndViewResolver(fxWeaver, bundle)
                .resolve(injectionPoint);
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
