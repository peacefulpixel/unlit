package me.ppixel.unlit;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import me.ppixel.unlit.annotation.TemplatableType;
import me.ppixel.unlit.exception.UnknownTypeException;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.tuple.Pair.of;

public class UnlitTypeProvider {
    private static final Map<String, Class<? extends Component>> compsByClassName = new HashMap<>();
    private static final Map<String, Class<? extends Component>> compsByFullClassName = new HashMap<>();

    @SuppressWarnings("unchecked")
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

        for (var pair : getKnownHtmlTags()) {
            compsByClassName.put(pair.getLeft(), pair.getRight());
        }
    }

    private Set<Pair<String, Class<? extends Component>>> getKnownHtmlTags() {
        return Set.of(
               of("a", Anchor.class),
               of("article", Article.class),
               of("aside", Aside.class),
               of("dl", DescriptionList.class),
               of("div", Div.class),
               of("em", Emphasis.class),
               of("footer", Footer.class),
               of("h1", H1.class),
               of("h2", H2.class),
               of("h3", H3.class),
               of("h4", H4.class),
               of("h5", H5.class),
               of("h6", H6.class),
               of("header", Header.class),
               of("hr", Hr.class),
               of("object", HtmlObject.class),
               of("iframe", IFrame.class),
               of("img", Image.class),
               of("input", Input.class),
               of("li", ListItem.class),
               of("main", Main.class),
               of("button", NativeButton.class),
               of("details", NativeDetails.class),
               of("label", NativeLabel.class),
               of("nav", Nav.class),
               of("ol", OrderedList.class),
               of("p", Paragraph.class),
               of("param", Param.class),
               of("pre", Pre.class),
               of("section", Section.class),
               of("span", Span.class),
               of("ul", UnorderedList.class)
        );
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
