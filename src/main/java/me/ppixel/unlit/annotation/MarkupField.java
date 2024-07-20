package me.ppixel.unlit.annotation;

import java.lang.annotation.*;

/**
 * Connecting an annotated class field with an element from template this class associated with. The element name is
 * identified by pure html attribute "id" and should be unique per template.
 * If single template has multiple elements with the same "id", that could produce undefined behaviour, basically like
 * in pure html page in your browser.
 * The type of field should be the same as template's one, or just assignable (convertible to). For example, you may
 * have a field of type {@link com.vaadin.flow.component.textfield.TextFieldBase} which can be mapped to
 * TextField/PasswordField in template.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MarkupField {

    /**
     * @return the identifier of a template field
     */
    String value();
}
