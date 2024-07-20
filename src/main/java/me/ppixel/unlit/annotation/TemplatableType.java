package me.ppixel.unlit.annotation;

import java.lang.annotation.*;

/**
 * Makes annotated class usable in XML template files.
 * Note that extending {@link me.ppixel.unlit.MappedComponent} isn't necessary here, but annotated class should satisfy
 * following requirements:
 * - Should be a child type of {@link com.vaadin.flow.component.Component}
 * - Should nave public constructor with no parameters
 *
 * If annotated class has the same name as one of vaadin components, the last one will be overwritten. Even though, you
 * still can access it by specifying full path (including a package) in the XML template file. Example:
 * <com.vaadin.flow.component.textfield.TextField id="vaadin-tf" _placeholder="Name" />
 * <com.example.components.TextField id="my-tf" _placeholder="Second name" />
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface TemplatableType {
}
