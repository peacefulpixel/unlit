package me.ppixel.unlit;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import static me.ppixel.unlit.UnlitTypeProvider.getConstructorWithNoParameters;
import static org.junit.jupiter.api.Assertions.*;

public class UnlitTypeProviderTest {

    @Test
    public void testGetType() {
        final var tp = new UnlitTypeProvider();
        assertEquals(tp.getType(Button.class.getName()), Button.class);
        assertEquals(tp.getType(Button.class.getName()), tp.getType("Button"));
        assertNotEquals(tp.getType(TextField.class.getName()), tp.getType("PasswordField"));
        assertEquals(tp.getType("p"), Paragraph.class);
        assertEquals(tp.getType("a"), Anchor.class);
        assertThrows(NullPointerException.class, () -> tp.getType(null));
    }

    @Test
    public void testHasType() {
        final var tp = new UnlitTypeProvider();
        assertTrue(tp.hasType("Button"));
        assertTrue(tp.hasType(Button.class.getName()));
        assertTrue(tp.hasType("p"));
        assertTrue(tp.hasType("div"));
        assertFalse(tp.hasType(" div"));
        assertFalse(tp.hasType(java.awt.Button.class.getName()));
        assertThrows(NullPointerException.class, () -> tp.hasType(null));
    }

    @Test
    public void testGetConstructorWithNoParameters() {
        assertNull(getConstructorWithNoParameters(ComponentWithNoConstructor.class));
        assertNull(getConstructorWithNoParameters(ComponentWithPrivateConstructor.class));
        assertNotNull(getConstructorWithNoParameters(Button.class));
    }

    private static class ComponentWithNoConstructor extends Component {
        public ComponentWithNoConstructor(Object o) {}
    }

    private static class ComponentWithPrivateConstructor extends Component {
        private ComponentWithPrivateConstructor() {}
    }
}
