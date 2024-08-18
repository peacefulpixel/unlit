package me.ppixel.unlit.mapped_component_test_classes;

import com.vaadin.flow.component.textfield.TextField;
import me.ppixel.unlit.annotation.MarkupField;

public class OkComponentChild extends OkComponent {
    @MarkupField("tf2") public TextField textField2;
}
