package me.ppixel.unlit;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import me.ppixel.unlit.annotation.MapMarkup;
import me.ppixel.unlit.annotation.MarkupField;
import me.ppixel.unlit.exception.InvalidSourceException;
import me.ppixel.unlit.exception.MappingException;
import me.ppixel.unlit.parser.UnlitXmlParser;
import me.ppixel.unlit.parser.UnlitXMLElement;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public abstract class MappedComponent extends Component implements HasStyle, HasComponents {
    private final InstanceGenerator instanceGenerator = new InstanceGenerator();
    private final Map<String, Component> childrenWithIds = new HashMap<>();

    protected MappedComponent() {
        super();
        final var markup = this.getClass().getAnnotation(MapMarkup.class);
        init(markup == null ? this.getClass().getName() : markup.value());
    }

    /**
     * TODO
     * @param id
     * @return
     * @param <T>
     * @throws ClassCastException e
     */
    protected <T extends Component> T getComponentById(String id) {
        return (T) childrenWithIds.get(id);
    }

    private void init(String filePath) {
        final String resource;
        try {
            resource = IOUtils.resourceToString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InvalidSourceException("Source " + filePath + " doesn't exists", e);
        }

        final var parser = new UnlitXmlParser(resource);
        final var root = parser.parse();
        final var comp = createComponentTree(root);

        add(comp);
        injectFields();
    }

    private Component createComponentTree(UnlitXMLElement element) {
        final Component c = instanceGenerator.generate(element.type);
        element.parameters.forEach((k, v) -> handleParameter(c, k, v));
        element.children.forEach(ch -> {
            if (c instanceof HasComponents hasComponents) {
                hasComponents.add(createComponentTree(ch));
            } else {
                // TODO: Do
                System.out.println("Child is unputtalbe");
            }
        });

        return c;
    }

    private void handleParameter(Component c, String k, String v) {

        if (k.startsWith("_")) {
            instanceGenerator.initialize(c, k.substring(1), v);
            return;
        }

        if ("id".equals(k))
            childrenWithIds.put(v, c);

        c.getElement().setAttribute(k, v);
    }

    private void injectFields() {
        final var failedFields = Stream.of(this.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(MarkupField.class))
                .filter(f -> {
                    final var a = f.getAnnotation(MarkupField.class);
                    final var c = childrenWithIds.get(a.value());

                    if (c == null)
                        return true;

                    try {
                        if (!f.canAccess(this)) {
                            f.trySetAccessible();
                        }

                        f.set(this, c);
                    } catch (IllegalAccessException e) {
                        throw new MappingException(format("Unable to set field \"%s\" with value of type \"%s\"",
                                f.getName(), c.getClass().getName()), e);
                    }

                    return false;
                }).map(Field::getName)
                .toList();

        if (!failedFields.isEmpty()) {
            throw new MappingException("Unable to map following fields: " + String.join(", ", failedFields));
        }
    }
}
