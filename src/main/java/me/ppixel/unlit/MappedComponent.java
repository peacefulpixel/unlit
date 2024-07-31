package me.ppixel.unlit;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.shared.SlotUtils;
import com.vaadin.flow.component.template.Id;
import me.ppixel.unlit.annotation.MapMarkup;
import me.ppixel.unlit.annotation.MarkupField;
import me.ppixel.unlit.exception.InvalidSourceException;
import me.ppixel.unlit.exception.MappingException;
import me.ppixel.unlit.parser.UnlitXMLElement;
import me.ppixel.unlit.parser.UnlitXMLParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;

public abstract class MappedComponent extends Component implements HasStyle, HasComponents {
    private final InstanceGenerator instanceGenerator = new InstanceGenerator();
    private final Map<String, Component> childrenWithIds = new HashMap<>();

    protected MappedComponent() {
        super();
        init(getPathToTemplate());
    }

    /**
     * @return the component by an identifier from template
     * @param id the identifier
     * @throws ClassCastException when can't cast a template element to specified type <T>
     */
    protected <T extends Component> T getComponentById(String id) {
        return (T) childrenWithIds.get(id);
    }

    /**
     * @return the path to the XML template file
     */
    protected String getPathToTemplate() {
        final var t = this.getClass();
        final var resource = Optional.ofNullable(t.getAnnotation(MapMarkup.class))
                .map(MapMarkup::value)
                .orElseGet(() -> t.getSimpleName() + ".xml");

        final var resourceDir = Optional.ofNullable(System.getProperty("me.ppixel.unlit.template-dir"))
                .orElse("");

        return Path.of("/", resourceDir, resource).toString();
    }

    private void init(String filePath) {
        final String resource;
        try {
            resource = IOUtils.resourceToString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InvalidSourceException("Source " + filePath + " doesn't exists", e);
        }

        final var parser = new UnlitXMLParser(resource);
        final var root = parser.parse();
        final var comp = createComponentTree(root);

        add(comp);
        injectFields();
    }

    private Component createComponentTree(UnlitXMLElement element) {
        final Component c = instanceGenerator.generate(element.type);
        element.parameters.forEach((k, v) -> handleParameter(c, k, v));

        if (c instanceof AppLayout a)
            handleChildrenForAppLayout(a, element.children);
        else
            handleChildrenForComponent(c, element.children);

        return c;
    }

    private void handleParameter(Component c, String k, String v) {

        if (k.startsWith("_")) {
            if (!k.startsWith("__")) {
                instanceGenerator.initialize(c, k.substring(1), v);
                return;
            }

            k = k.substring(1);
        }

        if ("id".equals(k))
            childrenWithIds.put(v, c);

        c.getElement().setAttribute(k, v);
    }

    private void handleChildrenForComponent(Component c, List<UnlitXMLElement> children) {
        for (UnlitXMLElement ch : children) {
            final var chAsComp = createComponentTree(ch);
            if (ch.parameters.containsKey("slot")) {
                SlotUtils.addToSlot(c, ch.parameters.get("slot"), chAsComp);
            } else if (c instanceof HasComponents hasComponents) {
                hasComponents.add(chAsComp);
            } else {
                // Ignoring Text instance here, because they're usually empty strings or newlines from parsed XML file.
                // Also, it doesn't make a lot of sense to throw an exception when someone just left pure text inside of
                // XML tag.
                if (!(chAsComp instanceof Text)) {
                    throw new MappingException(String.format("Unable to add child %s to parent %s, because parent's " +
                                    "class doesn't implementing HasComponents or child is not slotted",
                            chAsComp.getClass(), c.getClass()));
                }
            }
        }
    }

    private void handleChildrenForAppLayout(AppLayout c, List<UnlitXMLElement> children) {
        final Div content = new Div();
        c.setContent(content);

        for (UnlitXMLElement ch : children) {
            final var chAsComp = createComponentTree(ch);
            if (ch.parameters.containsKey("slot")) {
                SlotUtils.addToSlot(c, ch.parameters.get("slot"), chAsComp);
            } else {
                content.add(chAsComp);
            }
        }
    }

    private void injectFields() {
        final var failedFields = Stream.of(this.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(MarkupField.class) || f.isAnnotationPresent(Id.class))
                .filter(f -> {
                    final var v = Optional.ofNullable(f.getAnnotation(MarkupField.class))
                            .map(MarkupField::value)
                            .orElseGet(() -> f.getAnnotation(Id.class).value());
                    final var c = childrenWithIds.get(StringUtils.isEmpty(v) ? f.getName() : v);

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
