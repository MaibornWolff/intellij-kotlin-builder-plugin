package testdata

import kotlin.Boolean
import kotlin.Int
import kotlin.String

public class SimpleDataClassWithWrappedPrimitivesBuilder {
    private var intProperty: WrappedInt = WrappedInt(42)

    private var stringProperty: WrappedString = WrappedString("a string")

    private var booleanProperty: WrappedBoolean = WrappedBoolean(false)

    public fun build() = SimpleDataClassWithWrappedPrimitives(intProperty = intProperty,
                                                              stringProperty = stringProperty,
                                                              booleanProperty = booleanProperty)

    public fun withIntProperty(intProperty: WrappedInt) = apply { this.intProperty = intProperty }

    public fun withIntProperty(intProperty: Int) = apply { this.intProperty = WrappedInt(intProperty) }

    public fun withStringProperty(stringProperty: WrappedString) = apply { this.stringProperty = stringProperty }

    public fun withStringProperty(stringProperty: String) =
            apply { this.stringProperty = WrappedString(stringProperty) }

    public fun withBooleanProperty(booleanProperty: WrappedBoolean) =
            apply { this.booleanProperty = booleanProperty }

    public fun withBooleanProperty(booleanProperty: Boolean) =
            apply { this.booleanProperty = WrappedBoolean(booleanProperty) }
}