package me.ppixel.unlit;

import com.vaadin.flow.component.icon.Icon;
import me.ppixel.unlit.exception.InvalidTypeException;
import me.ppixel.unlit.exception.UnknownTypeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static me.ppixel.unlit.ParameterFactory.createParameter;
import static org.junit.jupiter.api.Assertions.*;

public class ParameterFactoryTest {

    @Test
    public void testInvalidParameters() {
        Assertions.assertThrows(UnknownTypeException.class,
                () -> createParameter(Void.class, "#String test"));
        Assertions.assertThrows(UnknownTypeException.class,
                () -> createParameter(Void.class, null));
        Assertions.assertThrows(UnknownTypeException.class,
                () -> createParameter(null, null));
    }

    @Test
    public void testString() {
        assertEquals("test", createParameter(String.class, "#String test"));
        assertEquals(" test", createParameter(String.class, "#String  test"));
        assertEquals("test", createParameter(String.class, "test"));
        assertEquals(" test", createParameter(String.class, " test"));
    }

    @Test
    public void testDouble() {
        assertEquals(1.0d, createParameter(Double.class, "#Double 1.0"));
        assertEquals(1.0d, createParameter(Double.class, "#Double  1.0"));
        assertEquals(1.0d, createParameter(Double.class, "1.0"));
        assertEquals(1.0d, createParameter(Double.class, "1"));
        assertEquals(1.0d, createParameter(Double.class, "+1"));
        assertEquals(-1.0d, createParameter(Double.class, "-1"));
        assertEquals(1.0d, createParameter(Double.class, "1d"));
        assertEquals(1.0d, createParameter(Double.class, "1.0d"));
        assertEquals(1.0d, createParameter(Double.class, " 1.0"));

        assertThrows(NumberFormatException.class, () -> createParameter(Double.class, ""));
        assertThrows(NumberFormatException.class, () -> createParameter(Double.class, "test"));
        assertThrows(NullPointerException.class, () -> createParameter(Double.class, null));
    }

    @Test
    public void testInteger() {
        assertEquals(1, createParameter(Integer.class, "#Integer 1"));
        assertEquals(1, createParameter(Integer.class, "#Integer  1"));
        assertEquals(1, createParameter(Integer.class, "1"));
        assertEquals(1, createParameter(Integer.class, "+1"));
        assertEquals(-1, createParameter(Integer.class, "-1"));
        assertEquals(1, createParameter(Integer.class, " 1"));

        assertThrows(NumberFormatException.class, () -> createParameter(Integer.class, ""));
        assertThrows(NumberFormatException.class, () -> createParameter(Integer.class, "1i"));
        assertThrows(NumberFormatException.class, () -> createParameter(Integer.class, "1.0"));
        assertThrows(NumberFormatException.class, () -> createParameter(Integer.class, "test"));
        assertThrows(NullPointerException.class, () -> createParameter(Integer.class, null));
    }

    @Test
    public void testBoolean() {
        assertEquals(true, createParameter(Boolean.class, "#Boolean true"));
        assertEquals(false, createParameter(Boolean.class, "#Boolean FALSE"));
        assertEquals(false, createParameter(Boolean.class, "#Boolean  FALSE"));
        assertEquals(false, createParameter(Boolean.class, "  FALSE"));
        assertEquals(true, createParameter(Boolean.class, "  TRUE  "));

        // Actually parseBoolean returns false whenever it can't parse the string, so there's no misbehaviour to test
        assertThrows(NullPointerException.class, () -> createParameter(Boolean.class, null));
    }

    @Test
    public void testIcon() {
        var o = createParameter(Icon.class, "#Icon vaadin:circle-o");
        var i = assertInstanceOf(Icon.class, o);
        assertEquals(i.getElement().getAttribute("icon"), "vaadin:circle-o");

        o = createParameter(Icon.class, "vaadin:circle-o");
        i = assertInstanceOf(Icon.class, o);
        assertEquals(i.getElement().getAttribute("icon"), "vaadin:circle-o");

        o = createParameter(Icon.class, "any-theme:any-icon");
        i = assertInstanceOf(Icon.class, o);
        assertEquals(i.getElement().getAttribute("icon"), "any-theme:any-icon");

        assertThrows(InvalidTypeException.class, () ->
                createParameter(Icon.class, "test"));
        assertThrows(InvalidTypeException.class, () ->
                createParameter(Icon.class, " vaadin:circle-o"));
        assertThrows(InvalidTypeException.class, () ->
                createParameter(Icon.class, " vaa1din:circle-1o"));
        assertThrows(InvalidTypeException.class, () ->
                createParameter(Icon.class, "_"));
        assertThrows(NullPointerException.class, () ->
                createParameter(Icon.class, null));
    }
}
