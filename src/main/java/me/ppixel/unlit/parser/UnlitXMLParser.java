package me.ppixel.unlit.parser;

import com.vaadin.flow.component.Text;
import me.ppixel.unlit.exception.InvalidSourceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class UnlitXMLParser {
    private final String source;

    public UnlitXMLParser(String source) {
        this.source = source;
    }

    public UnlitXMLElement parse() {
        final var doc = sourceToDocument();
        final var root = doc.getDocumentElement();
        return parse(root);
    }

    private UnlitXMLElement parse(Element element) {
        final var el = new UnlitXMLElement();
        el.type = element.getTagName();

        final var attrs = element.getAttributes();
        for (int x = 0; x < attrs.getLength(); x++) {
            final var item = attrs.item(x);
            if (Node.ATTRIBUTE_NODE != item.getNodeType())
                continue;

            final var attr = (Attr) item;
            el.parameters.put(attr.getName(), attr.getValue());
        }

        final var kids = element.getChildNodes();
        for (int x = 0; x < kids.getLength(); x ++) {
            final var item = kids.item(x);

            if (Node.TEXT_NODE == item.getNodeType())
                processTextNode(el, item);

            if (Node.ELEMENT_NODE != item.getNodeType())
                continue;

            final var kid = (Element) item;
            el.children.add(parse(kid));
        }

        return el;
    }

    private void processTextNode(UnlitXMLElement parent, Node node) {
        final var value = node.getTextContent()
                .replaceAll("^\n*", "")     // Newlines at beginning
                .replaceAll("\n*$", "")     // Newlines at the end
                .replaceAll("\n\\s*$", ""); // Empty lines

        if (value.isEmpty() || value.matches("^\\s*$"))
            return;

        final var text = new UnlitXMLElement();
        text.type = Text.class.getName();
        text.parameters.put("_text", value);
        parent.children.add(text);
    }

    private Document sourceToDocument() {
        try {
            final var factory = DocumentBuilderFactory.newInstance();
            final var builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(source)));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new InvalidSourceException("Unable to parse source: " + source, e);
        }
    }
}
