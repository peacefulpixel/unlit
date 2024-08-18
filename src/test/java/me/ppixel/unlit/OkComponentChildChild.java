package me.ppixel.unlit;

import com.vaadin.flow.component.textfield.TextField;
import me.ppixel.unlit.annotation.MarkupField;
import me.ppixel.unlit.mapped_component_test_classes.OkComponentChild;

public class OkComponentChildChild extends OkComponentChild {
    @MarkupField("tf1") public TextField textField1;
    @MarkupField("tf2") public TextField textField3;
}
