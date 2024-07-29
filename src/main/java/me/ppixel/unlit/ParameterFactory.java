package me.ppixel.unlit;

import com.vaadin.flow.component.icon.Icon;
import me.ppixel.unlit.exception.InvalidTypeException;
import me.ppixel.unlit.exception.UnknownTypeException;
import org.apache.commons.lang3.StringUtils;

public class ParameterFactory {

    public static Object createParameter(Class<?> type, String rawParameter) {
        if (StringUtils.startsWith(rawParameter, "#"))
            rawParameter = rawParameter.replaceAll("^#[a-zA-Z]+ ", "");

        if (String.class.equals(type))     return rawParameter;
        if (Double.class.equals(type))     return createDouble(rawParameter);
        if (Integer.class.equals(type))    return createInteger(rawParameter);
        if (Boolean.class.equals(type))    return createBoolean(rawParameter);
        if (Icon.class.equals(type))       return createIcon(rawParameter);

        throw new UnknownTypeException("Unsupported type for parameter: " + type);
    }

    private static Double createDouble(String raw) {
        return Double.parseDouble(raw);
    }

    private static Integer createInteger(String raw) {
        return Integer.parseInt(raw.trim());
    }

    private static Boolean createBoolean(String raw) {
        return Boolean.parseBoolean(raw.trim());
    }

    private static Icon createIcon(String raw) {
        if (!raw.matches("^[a-z-]+:[a-z-]+$"))
            throw new InvalidTypeException("Invalid icon: " + raw + ". The icon path should contain collection and " +
                    "name separated by colon (e.g vaadin:circle)");

        final var parts = raw.split(":");

        return new Icon(parts[0], parts[1]);
    }
}
