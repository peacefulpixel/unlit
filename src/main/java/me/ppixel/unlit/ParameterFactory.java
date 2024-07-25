package me.ppixel.unlit;

import com.vaadin.flow.component.icon.Icon;
import me.ppixel.unlit.exception.InvalidTypeException;
import me.ppixel.unlit.exception.UnknownTypeException;

public class ParameterFactory {

    public static Object createParameter(Class<?> type, String rawParameter) {
        if (rawParameter.startsWith("#"))
            rawParameter = rawParameter.replaceAll("^#[a-zA-Z]+ ", "");

        if (String.class.equals(type))     return rawParameter;
        if (Double.class.equals(type))     return createDouble(rawParameter);
        if (Integer.class.equals(type))    return createInteger(rawParameter);
        if (Icon.class.equals(type))       return createIcon(rawParameter);

        throw new UnknownTypeException("Unsupported type for parameter: " + type.getName());
    }

    private static Double createDouble(String raw) {
        return Double.parseDouble(raw);
    }

    private static Integer createInteger(String raw) {
        return Integer.parseInt(raw);
    }

    private static Icon createIcon(String raw) {
        if (!raw.matches("^[a-z-]+:[a-z-]+$"))
            throw new InvalidTypeException("Invalid icon: " + raw + ". The icon path should contain collection and " +
                    "name separated by colon (e.g vaadin:circle)");

        final var parts = raw.split(":");

        return new Icon(parts[0], parts[1]);
    }
}
