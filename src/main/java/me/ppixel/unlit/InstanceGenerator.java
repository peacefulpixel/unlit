package me.ppixel.unlit;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.Icon;
import me.ppixel.unlit.exception.InvalidTypeException;
import me.ppixel.unlit.exception.MappingException;
import me.ppixel.unlit.exception.UnknownTypeException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class InstanceGenerator {
    private final UnlitTypeProvider typeProvider = new UnlitTypeProvider();

    public Component generate(String name) {
        if (typeProvider.hasType(name)) {
            final var type = typeProvider.getType(name);
            try {
                return type.getConstructor().newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new InvalidTypeException("Unable to instance the type: " + name, e);
            }
        } else {
            return generateUndefinedComponent(name);
        }
    }

    private Component generateUndefinedComponent(String name) {
        if (Text.class.getName().equals(name) || Text.class.getSimpleName().equals(name)) {
            return new Text("");
        }

        // Simple HTML tag
        if (name.startsWith((name.charAt(0) + "").toLowerCase())) {
            return new HtmlContainer(name);
        }

        throw new UnknownTypeException("Unknown type: " + name);
    }

    public void initialize(Component component, String property, String value) {
        final var methodName = "set" + StringUtils.capitalize(property);
        final var type = component.getClass();

        final var methods = new HashSet<Pair<Class<?>, Method>>();
        for (var method : type.getMethods()) {
            if (!methodName.equals(method.getName()))
                continue;

            if (method.getParameterCount() != 1)
                continue;

            methods.add(Pair.of(method.getParameters()[0].getType(), method));
        }

        if (methods.isEmpty())
            throw new MappingException("Unable to find method " + methodName + " in class " + component.getClass());

        final var match = findMatch(methods, value);
        final var m = match.getLeft();
        final var v = ParameterFactory.createParameter(match.getRight(), value);

        try {
            m.invoke(component, v);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(format("Unable to set property \"%s\" with value \"%s\"",
                    property, value), e);
        }
    }

    private Pair<Method, Class<?>> findMatch(Set<Pair<Class<?>, Method>> methods, String value) {
        final var paramTypes = getAvailableTypesForValue(value);

        for (Pair<Class<?>, Method> method : methods) {
            for (Class<?> paramAvailableType : paramTypes) {
                var type = method.getKey();
                type = type.isPrimitive() ? ClassUtils.primitiveToWrapper(type) : type;
                if (type.isAssignableFrom(paramAvailableType))
                    return Pair.of(method.getRight(), paramAvailableType);
            }
        }

        throw new UnknownTypeException("Can't find setter for type " +
                paramTypes.stream()
                .map(Class::getName)
                .collect(Collectors.joining(", ")) + " (value " + value + ")");
    }

    private Set<Class<?>> getAvailableTypesForValue(String value) {

        if (value.startsWith("#")) {
            if (value.startsWith("#Double"))     return Set.of(Double.class);
            if (value.startsWith("#Integer"))    return Set.of(Integer.class);
            if (value.startsWith("#Boolean"))    return Set.of(Boolean.class);
            if (value.startsWith("#String"))     return Set.of(String.class);
            if (value.startsWith("#Icon"))       return Set.of(Icon.class);

            throw new UnknownTypeException(value);
        }

        final var types = new HashSet<Class<?>>();
        types.add(String.class);

        if (value.matches("^[-+]?[0-9]+(\\.[0-9]+)?$")) {
            types.add(Double.class);

            if (!value.contains("."))
                types.add(Integer.class);
        }

        if (value.matches("^(true|TRUE|false|FALSE)$"))
            types.add(Boolean.class);

        if (value.matches("^[a-z-]+:[a-z-]+$"))
            types.add(Icon.class);

        return types;
    }
}
