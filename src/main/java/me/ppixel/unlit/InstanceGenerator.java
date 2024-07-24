package me.ppixel.unlit;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Text;
import me.ppixel.unlit.exception.InvalidTypeException;
import me.ppixel.unlit.exception.MappingException;
import me.ppixel.unlit.exception.UnknownTypeException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        final var isNumeric = value.matches("^[-+]?[0-9]+(\\.[0-9]+)?$");
        final var methodName = "set" + StringUtils.capitalize(property);
        final var type = component.getClass();

        boolean isMethodNumeric = false;
        Method m = null;
        for (var method : type.getMethods()) {
            if (!methodName.equals(method.getName()))
                continue;

            if (method.getParameterCount() != 1)
                continue;

            if (method.getParameters()[0].getType().isAssignableFrom(String.class)) {
                m = method;
                if (!isNumeric) break;
            }

            if (Number.class.isAssignableFrom(method.getParameters()[0].getType()) && isNumeric) {
                m = method;
                isMethodNumeric = true;
                break;
            }
        }

        if (m == null)
            throw new MappingException("Unable to find method " + methodName + " in class " + component.getClass());

        final Object val;
        if (!isMethodNumeric) {
            val = value;
        } else {
            if (value.contains(".")) {
                val = Double.parseDouble(value);
            } else {
                val = Integer.parseInt(value);
            }
        }

        try {
            m.invoke(component, val);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(format("Unable to set property \"%s\" with value \"%s\"",
                    property, value), e);
        }
    }
}
