package me.ppixel.unlit.mapped_component_test_classes;

import com.vaadin.flow.component.Tag;
import me.ppixel.unlit.MappedComponent;
import me.ppixel.unlit.annotation.MapMarkup;

@Tag("div")
@MapMarkup("templates/FailComponentWrongSetter.xml")
public class FailComponentWrongSetter extends MappedComponent {
}
