package drrename;

import javafx.scene.Node;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.LazyFxControllerAndView;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.core.ResolvableType;

import java.util.Optional;
import java.util.ResourceBundle;

public class ResourceBundleAwareLazyFxControllerAndViewResolver {

    private final FxWeaver fxWeaver;
    private final ResourceBundle resourceBundle;

    public ResourceBundleAwareLazyFxControllerAndViewResolver(FxWeaver fxWeaver, ResourceBundle resourceBundle) {
        this.fxWeaver = fxWeaver;
        this.resourceBundle = resourceBundle;
    }

    public <C, V extends Node> FxControllerAndView<C, V> resolve(InjectionPoint injectionPoint) {
        ResolvableType resolvableType = findResolvableType(injectionPoint);
        if (resolvableType == null) {
            throw new IllegalArgumentException("No ResolvableType found");
        }
        try {
            @SuppressWarnings("unchecked")
            Class<C> controllerClass = (Class<C>) resolvableType.getGenerics()[0].resolve();
            return new LazyFxControllerAndView<>(() -> fxWeaver.load(controllerClass, resourceBundle));
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Generic controller type not resolvable for injection point " + injectionPoint, e);
        }
    }

    private ResolvableType findResolvableType(InjectionPoint injectionPoint) {
        return Optional.ofNullable(injectionPoint.getMethodParameter())
                .map(ResolvableType::forMethodParameter)
                // TODO: Refactor the following to use .or() when dropping Java 8 support
                .orElse(
                        Optional.ofNullable(injectionPoint.getField())
                                .map(ResolvableType::forField)
                                .orElse(null)
                );
    }
}
