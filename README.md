# Unlit
Unlit is library for creating vaadin components from XML templates. It parses XML hierarchy and uses the tag names to
determinate classes of Vaadin components. Then, it creates instances, sets properties/fields and attaches that to the
root of your class (which should extend `Component`).

### Why?
Vaadin already has an internal templating engine, which is running on **Lit JavaScript framework**. This also brings
overcomplexity such as npm compilation, writing JavaScript/TypeScript sources, shadow-root components encapsulation and
other similar things from the "front-end" world.

Unlit - is a simple & straightforward solution that covers only single responsibility - providing XML templates that
could be mapped on Java classes.

#### How different is that from just using Lit?
XML templates are simpler and definitely faster to create. The mapper is straight-forward and doesn't hide any 
unexpected logic.

#### This project doesn't have official Vaadin support, but Lit does! So how can I trust that?
The code is open source, and what is more important - it's tight. You can investigate whole project within 15 minutes
if you want to clarify how things work. Actually there's no need to do that, because the library, yet again, really
straightforward and simple solution, which doesn't have any complicated architecture.

*Also, Unlit doesn't use Spring to avoid implicity for you.*

## Get started
You can get started just here, and there's (should be) no need to google anything else.

### XML templates
The XML template by itself is an XML file within the classpath. Typically, in Vaadin app, you can create them in the
*src/main/resources* dir. The contents of XML file looks like:
```XML
<?xml version="1.0" encoding="UTF-8" ?>
<VerticalLayout theme="spacing">
    <HorizontalLayout style="width:100%">
        <TextField id="tf-search" _placeholder="Search" style="flex-grow:1" />
        <Button id="search-submit" _text="Search" theme="primary" />
    </HorizontalLayout>
    <Grid id="main-grid" />
</VerticalLayout>
```
- **Tag name** - is a name of the class located somewhere within `com.vaadin.flow.component`. You can also provide full 
  path with a package
- **Parameters** - all the parameters and their values will be directly assigned to a component's `Element`, except of
  those who starting with `_`.
- **Parameters with _underscore** - are shortcuts for component's setters. For instance, `_placeholder="Search"` is
  equal to `component.setPlaceholder("Search");` in Java. If you need an element property started with underscore for
  some reason, just use double underscore like `__elementAttr="xyz"`.

#### Tag names
Usually, tag names doesn't have any collision within `com.vaadin.flow.component`, but if they do, you can specify full
name with the package like `<com.vaadin.flow.component.orderedlayout.VerticalLayout />`.

You can also define your components and use them in XML templates, to do that you need to annotate them with 
`me.ppixel.unlit.annotation.TemplatableType`. Also, this feature doesn't require you component to extend 
`MappedComponent`, but requires to extend `Component` and have public constructor with no args. For example, you can do
that:

```Java
@TemplatableType
public class MyComponent extends VerticalLayout {
}
```
```XML
<?xml version="1.0" encoding="UTF-8" ?>
<VerticalLayout theme="spacing">
    <MyComponent style="width:100%">
    </MyComponent>
</VerticalLayout>
```

#### Parameters with _underscore and setter types
Some methods of Vaadin components are setters, but not of `String` type. Unlit handles that when it processes the value
of attribute. It checks the type of setter parameter and if it's an `Integer`, for instance, it tries to perform the
cast. So if you're using something like an `IntegerField`, you can do this:
```XML
<?xml version="1.0" encoding="UTF-8" ?>
<VerticalLayout theme="padding spacing">
    <IntegerField style="width:100%" _value="100" />
</VerticalLayout>
```
The table of supported types:

| Java type | Regex pattern              | Example            |
|-----------|----------------------------|--------------------|
| String    | `.*`                       | Hello              |
| Integer   | `^[-+]?[0-9]+$`            | 123                |
| Double    | `^[-+]?[0-9]+(\.[0-9]+)?$` | 1.23               |
| Icon      | `^[a-z-]+:[a-z-]+$`        | vaadin:circle-thin |

### Component classes
After you created a template, you also need to create a Java class to map that on. Mapped classes typically looks like 
that:
```Java
@Tag("div")
@MapMarkup("/templates/my-component.xml")
public class MyComponent extends MappedComponent {
    @MarkupField("tf-search")     private TextField tfSearch;
    @MarkupField("search-submit") private Button btnSearch;
    @MarkupField("main-grid")     private Grid<?> gMain;
}
```
- `@Tag` - is a Vaadin annotation that assigns your component an element with specified tag. Unfortunately, we can't
  avoid this, because element should be instanced with its component parent, and can't be changed during runtime. So we
  simply add whatever you defined in your XML template to this component.
- `MappedComponent` - is a parent class, that is required to extend, because it performs mapping within its constructor. 
- `@MapMarkup` - this annotation explicitly saying the path of your XML template.
- `@MarkupField` - work similar to the `@Id` from Vaadin. It searches the element of your template by attribute `id`.
  Note, that it doesn't use DOM or actual HTML element tree that you see in your browser, but simply searches for
  component you defined in template with corresponding `id`.

### Advanced features
#### Using different types in setters
Unlit provides you a feature to explicitly assign type for your parameter with the `#` prefix:
```XML
<IntegerField _value="#Integer 1" />
<TextField _value="#String 1" />
```
Usually, it's not needed, but if you have a custom component where you have two setters with same name, but different
parameter type, here it goes.
#### Slots
You can set slots for your component from the XML template.
```XML
<Button id="someBtn" _text="Slot Test">
  <p slot="prefix">PFX</p>
  <p slot="suffix">SFX</p>
</Button>
```
You may notice that Button isn't a container, so it couldn't have any children. That's correct so Unlit not even tries
to add it inside. Although, if component **is a container**, the slotted item will be added as a child.