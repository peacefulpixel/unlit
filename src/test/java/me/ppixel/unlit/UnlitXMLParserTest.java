package me.ppixel.unlit;

import com.vaadin.flow.component.Text;
import me.ppixel.unlit.exception.InvalidSourceException;
import me.ppixel.unlit.parser.UnlitXMLElement;
import me.ppixel.unlit.parser.UnlitXMLParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnlitXMLParserTest {
    private final String regularTreeSource = """
            <Root>
              <Element>Text1</Element>
              <Element />Text2
              Text3
              <ElementX param="x" paramX="">
                Text4
                <ElementY param="y"><ElementZ /></ElementY>
              </ElementX>
            </Root>
            """;

    private final UnlitXMLElement regularTree;

    public UnlitXMLParserTest() {
        final var parser = new UnlitXMLParser(regularTreeSource);
        regularTree = parser.parse();
    }

    private void assertTreesAreEqual(UnlitXMLElement root1, UnlitXMLElement root2) {
        assertEquals(root1.type, root2.type);
        assertEquals(root1.children.size(), root2.children.size());
        assertEquals(root1.parameters.size(), root2.parameters.size());

        for (int x = 0; x < root1.children.size(); x++)
            assertTreesAreEqual(root1.children.get(x), root2.children.get(x));

        for (var k : root1.parameters.keySet())
            assertEquals(root1.parameters.get(k), root2.parameters.get(k));
    }

    private void assertIsText(UnlitXMLElement node, String value) {
        assertEquals(Text.class.getName(), node.type);
        assertTrue(node.children.isEmpty());
        assertEquals(1, node.parameters.size());
        assertTrue(node.parameters.containsKey("_text"));
        assertEquals(value, node.parameters.get("_text"));
    }

    @Test
    public void testValidSimple() {
        final var src = """
            <Node1 node1attr="node1attrVal">
              <Node2 />
            </Node1>
            """;
        final var parser = new UnlitXMLParser(src);
        final var root = parser.parse();

        assertEquals("Node1", root.type);
        assertEquals(1, root.parameters.size());
        assertTrue(root.parameters.containsKey("node1attr"));
        assertEquals("node1attrVal", root.parameters.get("node1attr"));
        assertEquals(1, root.children.size());
        assertEquals("Node2", root.children.get(0).type);
        assertTrue(root.children.get(0).children.isEmpty());
        assertTrue(root.children.get(0).parameters.isEmpty());
    }

    @Test
    public void testValidRegular() {
        final var parser = new UnlitXMLParser(regularTreeSource);
        final var root = parser.parse();

        assertEquals("Root", root.type);
        assertTrue(root.parameters.isEmpty());
        assertEquals(4, root.children.size());

        assertEquals("Element", root.children.get(0).type);
        assertTrue(root.children.get(0).parameters.isEmpty());
        assertEquals(1, root.children.get(0).children.size());
        assertIsText(root.children.get(0).children.get(0), "Text1");

        assertEquals("Element", root.children.get(1).type);
        assertTrue(root.children.get(1).parameters.isEmpty());
        assertTrue(root.children.get(1).children.isEmpty());

        assertIsText(root.children.get(2), "Text2\n  Text3");

        assertEquals("ElementX", root.children.get(3).type);
        assertEquals(2, root.children.get(3).parameters.size());
        assertEquals("x", root.children.get(3).parameters.get("param"));
        assertEquals("", root.children.get(3).parameters.get("paramX"));
        assertEquals(2, root.children.get(3).children.size());
        assertIsText(root.children.get(3).children.get(0), "    Text4");
        assertEquals("ElementY", root.children.get(3).children.get(1).type);
        assertEquals(1, root.children.get(3).children.get(1).parameters.size());
        assertEquals("y", root.children.get(3).children.get(1).parameters.get("param"));
        assertEquals(1, root.children.get(3).children.get(1).children.size());
        assertEquals("ElementZ", root.children.get(3).children.get(1).children.get(0).type);
        assertTrue(root.children.get(3).children.get(1).children.get(0).parameters.isEmpty());
        assertTrue(root.children.get(3).children.get(1).children.get(0).children.isEmpty());
    }


    @Test
    public void testDocTypeNotChangesResult() {
        final var src = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + regularTreeSource;
        final var parser = new UnlitXMLParser(src);
        assertTreesAreEqual(parser.parse(), regularTree);
    }

    @Test
    public void testWhitespacesNotChangeResult() {
        final var src = """
            <Root>
            
              <Element>Text1</Element>
              <Element />Text2
              Text3
              <ElementX param="x"
                    paramX="">
                Text4
                <ElementY param="y"><ElementZ /></ElementY>
              </ElementX>
              
              
            </Root>
            """;

        final var parser = new UnlitXMLParser(src);
        assertTreesAreEqual(parser.parse(), regularTree);
    }

    @Test
    public void testBreakOnInvalidTag() {
        final var src = """
            <Root>
              <1>
              </1>
            </Root>
            """;

        final var parser = new UnlitXMLParser(src);
        assertThrows(InvalidSourceException.class, parser::parse);
    }

    @Test
    public void testBreakOnInvalidParam() {
        final var src = """
            <Root>
              <X p>
              </X>
            </Root>
            """;

        final var parser = new UnlitXMLParser(src);
        assertThrows(InvalidSourceException.class, parser::parse);
    }

    @Test
    public void testBreakOnInvalidParamSet() {
        final var src = """
            <Root>
              <X p=>
              </X>
            </Root>
            """;

        final var parser = new UnlitXMLParser(src);
        assertThrows(InvalidSourceException.class, parser::parse);
    }

    @Test
    public void testBreakOnParamValueNoBrackets() {
        final var src = """
            <Root>
              <X p=T>
              </X>
            </Root>
            """;

        final var parser = new UnlitXMLParser(src);
        assertThrows(InvalidSourceException.class, parser::parse);
    }
}
