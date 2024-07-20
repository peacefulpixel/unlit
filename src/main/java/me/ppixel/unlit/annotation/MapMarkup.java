package me.ppixel.unlit.annotation;

import java.lang.annotation.*;

/**
 * Associates annotated class with exact XML template file in the classpath.
 * A class by itself should always have {@link me.ppixel.unlit.MappedComponent} as a parent, because it handles this
 * annotation inside its constructor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MapMarkup {

    /**
     * @return path to XML template file
     */
    String value();
}
