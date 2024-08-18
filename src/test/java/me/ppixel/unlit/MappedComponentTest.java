package me.ppixel.unlit;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import me.ppixel.unlit.annotation.MarkupField;
import me.ppixel.unlit.exception.MappingException;
import me.ppixel.unlit.exception.NoSuchSourceException;
import me.ppixel.unlit.mapped_component_test_classes.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class MappedComponentTest {

    @BeforeEach
    public void beforeEach() {
        System.clearProperty("me.ppixel.unlit.template-dir");
    }

    @Test
    public void testGetComponentById() {
        final var comp = new TestGetComponentByIdComponent();

        final Button btn1 = comp.getComponentByIdProxy("btn1");
        final TextField tf1 = comp.getComponentByIdProxy("tf1");
        final TextField tf2 = comp.getComponentByIdProxy("tf2");

        assertNotNull(btn1);
        assertNotNull(tf1);
        assertNotNull(tf2);
        assertNotEquals(tf1, tf2);
    }

    @Test
    public void testGetComponentByIdFailsOnWrongId() {
        final var comp = new TestGetComponentByIdComponent();

        assertThrows(NoSuchElementException.class, () -> {
            comp.getComponentByIdProxy("123");
        });

        assertThrows(NoSuchElementException.class, () -> {
            comp.getComponentByIdProxy("");
        });

        assertThrows(NoSuchElementException.class, () -> {
            comp.getComponentByIdProxy(null);
        });
    }

    @Test
    public void testPathVariable() {
        assertThrows(NoSuchSourceException.class, OkComponentNoMapMarkup::new);
        assertDoesNotThrow(OkComponent::new);
        System.setProperty("me.ppixel.unlit.template-dir", "/templates");
        Assertions.assertDoesNotThrow(OkComponentNoMapMarkup::new);
        assertThrows(NoSuchSourceException.class, OkComponent::new);
    }

    @Test
    public void testFailIfNoTag() {
        assertThrows(IllegalStateException.class, () -> new MappedComponent() {});
        assertThrows(IllegalStateException.class, NoTagComponent::new);
    }

    @Test
    public void testFailIfNoTemplate() {
        assertThrows(NoSuchSourceException.class, FailComponentNoMarkup::new);
        assertThrows(NoSuchSourceException.class, FailComponentWithMarkup::new);
    }

    @Test
    public void testFailIfNoSetter() {
        assertThrows(MappingException.class, FailComponentWrongSetter::new);
    }

    @Test
    public void testFailIfCantAddChild() {
        assertThrows(MappingException.class, FailComponentCantAddChild::new);
    }

    @Test
    public void testFieldsAreInjected() {
        final var comp = new OkComponent() {
            @MarkupField("btn1") private Button button1;
            @MarkupField("tf1") private TextField textField1;
            @MarkupField("tf2") private TextField textField2;
        };

        assertNotNull(comp.button1);
        assertNotNull(comp.textField1);
        assertNotNull(comp.textField2);
        assertNotEquals(comp.textField1, comp.textField2);
    }

    @Test
    public void testFailOnWrongFieldInjection() {
        assertThrows(MappingException.class, () -> new OkComponent() {
            @MarkupField("btn1") private Button button1;
            @MarkupField("tf3") private TextField textField3; // No such field
            @MarkupField("tf2") private TextField textField2;
        });
    }

    @Test
    public void testHierarchicalInjecting() {
        final var comp = new OkComponentChildChild();

        assertNotNull(comp.textField1);
        assertNotNull(comp.textField2);
        assertNotNull(comp.textField3);
        assertSame(comp.textField2, comp.textField3); // Reference should be exactly same (not just equals)
        assertNotSame(comp.textField2, comp.textField1);
    }
}
