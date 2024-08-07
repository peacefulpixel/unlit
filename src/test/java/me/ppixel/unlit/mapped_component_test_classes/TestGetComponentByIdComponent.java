package me.ppixel.unlit.mapped_component_test_classes;

public class TestGetComponentByIdComponent extends OkComponent {

    public <T> T getComponentByIdProxy(String id) {
        return (T) getComponentById(id);
    }
}
