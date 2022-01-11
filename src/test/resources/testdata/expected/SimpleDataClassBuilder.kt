package testdata

import kotlin.Boolean
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.String

public class SimpleDataClassBuilder {

    private var intProperty: Int = 42

    private var stringProperty: String = "a string"

    private var booleanProperty: Boolean = false

    private var doubleProperty: Double = 1.0

    private var floatProperty: Float = 1.0F

    public fun build() = SimpleDataClass(intProperty = intProperty,
                                         stringProperty = stringProperty,
                                         booleanProperty = booleanProperty,
                                         doubleProperty = doubleProperty,
                                         floatProperty = floatProperty)

    public fun withIntProperty(intProperty: Int) = apply { this.intProperty = intProperty }

    public fun withStringProperty(stringProperty: String) = apply {
        this.stringProperty = stringProperty
    }

    public fun withBooleanProperty(booleanProperty: Boolean) = apply {
        this.booleanProperty = booleanProperty
    }

    public fun withDoubleProperty(doubleProperty: Double) = apply {
        this.doubleProperty = doubleProperty
    }

    public fun withFloatProperty(floatProperty: Float) = apply {
        this.floatProperty = floatProperty
    }
}
