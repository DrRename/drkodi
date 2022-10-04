package drrename;

import drrename.ui.ResourceBundleAwareLazyFxControllerAndViewResolver;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Locale;
import java.util.ResourceBundle;

@Slf4j
@SpringBootApplication
public class Launcher {

    public static void main(String[] args) {
        DrRenameApplication.main(args);
    }

    @Bean
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        // Would also work with javafx-weaver-core only:
        // return new FxWeaver(applicationContext::getBean, applicationContext::close);
        return new SpringFxWeaver(applicationContext);
    }

    @Bean
    public ResourceBundle bundle() {
        // My default is "ENGLISH". Instead of changing my computer's timezone for testing,
        // I'm programmatically setting it here. In live code, I imagine one would set
        // Locale locale = Locale.getDefault()

        Locale locale = Locale.GERMAN;
        log.debug("Locale: {}", locale);
        return ResourceBundle.getBundle("i18n/messages", locale);
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
                                                                           InjectionPoint injectionPoint) {
        return new ResourceBundleAwareLazyFxControllerAndViewResolver(fxWeaver, bundle())
                .resolve(injectionPoint);
    }
}
