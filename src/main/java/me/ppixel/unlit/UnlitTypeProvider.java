package me.ppixel.unlit;

import com.vaadin.flow.component.Component;
import me.ppixel.unlit.annotation.TemplatableType;
import me.ppixel.unlit.exception.UnknownTypeException;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class UnlitTypeProvider {
    private static final Map<String, Class<? extends Component>> compsByClassName = new HashMap<>();
    private static final Map<String, Class<? extends Component>> compsByFullClassName = new HashMap<>();

    public UnlitTypeProvider() {
        if (!compsByClassName.isEmpty() && !compsByFullClassName.isEmpty())
            return;

        final var refsVaadin = new Reflections("com.vaadin.flow.component");
        final var allVaadinComponents = refsVaadin.getSubTypesOf(Component.class);

        final var refsCore = new Reflections();
        final var allTemplatableTypes = refsCore.getTypesAnnotatedWith(TemplatableType.class);

        for (var comp : allVaadinComponents) {
            if (getConstructorWithNoParameters(comp) != null)
                storeType(comp);
        }

        for (var comp : allTemplatableTypes) {
            if (!Component.class.isAssignableFrom(comp)) {
                //TODO: print warn
                return;
            }

            storeType((Class<? extends Component>) comp);
        }
    }

    private void storeType(Class<? extends Component> type) {
        compsByClassName.put(type.getSimpleName(), type);
        compsByFullClassName.put(type.getName(), type);
    }

    public Class<? extends Component> getType(String name) {
        final var map = name.contains(".") ? compsByFullClassName : compsByClassName;
        if (!map.containsKey(name))
            throw new UnknownTypeException("Unknown type: " + name);

        return map.get(name);
    }

    public boolean hasType(String name) {
        return name.contains(".") ? compsByFullClassName.containsKey(name) : compsByClassName.containsKey(name);
    }

    /**
     * @return constructor or null if no such
     */
    public static Constructor<? extends Component> getConstructorWithNoParameters(Class<? extends Component> type) {
        try {
            return type.getConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
