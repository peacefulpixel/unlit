package me.ppixel.unlit.mapped_component_test_classes;

import com.vaadin.flow.component.Tag;
import me.ppixel.unlit.MappedComponent;
import me.ppixel.unlit.annotation.MapMarkup;

@Tag("div")
@MapMarkup("templates/FailComponentCantAddChild.xml")
public class FailComponentCantAddChild extends MappedComponent {
}
