package testdata

public class SimpleDataClassWithWrappedPrimitivesBuilder {
    private var intProperty: WrappedInt = WrappedInt(42)

    private var stringProperty: WrappedString = WrappedString("a string")

    private var booleanProperty: WrappedBoolean = WrappedBoolean(false)

    public fun build() = SimpleDataClassWithWrappedPrimitives(intProperty = intProperty,
                                                              stringProperty = stringProperty,
                                                              booleanProperty = booleanProperty)

    public fun withIntProperty(intProperty: WrappedInt) = apply { this.intProperty = intProperty }

    public fun withStringProperty(stringProperty: WrappedString) = apply { this.stringProperty =
        stringProperty }

    public fun withBooleanProperty(booleanProperty: WrappedBoolean) = apply { this.booleanProperty =
        booleanProperty }
}
