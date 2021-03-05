package testdata

public class SimpleDataClassBuilder {
    private var intProperty: Int = 42

    private var stringProperty: String = "a string"

    private var booleanProperty: Boolean = false

    public fun build() = SimpleDataClass(intProperty = intProperty,
                                         stringProperty = stringProperty,
                                         booleanProperty = booleanProperty)

    public fun withIntProperty(intProperty: Int) = apply { this.intProperty = intProperty }

    public fun withStringProperty(stringProperty: String) = apply { this.stringProperty =
        stringProperty }

    public fun withBooleanProperty(booleanProperty: Boolean) = apply { this.booleanProperty =
        booleanProperty }
}
