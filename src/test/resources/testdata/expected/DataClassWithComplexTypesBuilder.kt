package testdata

import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

public class DataClassWithComplexTypesBuilder {

    private var property1: PropertyType1 = TODO("Needs a default value!")

    private var property2: PropertyType2 = TODO("Needs a default value!")

    private var listProperty: List<String> = emptyList()

    private var mapOfComplexTypeProperty: Map<Int, PropertyType2> = emptyMap()

    private var nullableProperty: String? = null

    public fun build() = DataClassWithComplexTypes(
        property1 = property1,
        property2 = property2,
        listProperty = listProperty,
        mapOfComplexTypeProperty = mapOfComplexTypeProperty,
        nullableProperty = nullableProperty
                                                  )

    public fun withProperty1(property1: PropertyType1) = apply { this.property1 = property1 }

    public fun withProperty2(property2: PropertyType2) = apply { this.property2 = property2 }

    public fun withListProperty(listProperty: List<String>) = apply {
        this.listProperty = listProperty
    }

    public fun withMapOfComplexTypeProperty(mapOfComplexTypeProperty: Map<Int, PropertyType2>) = apply {
        this.mapOfComplexTypeProperty = mapOfComplexTypeProperty }

    public fun withNullableProperty(nullableProperty: String) = apply {
        this.nullableProperty =
            nullableProperty
    }

    public fun withoutNullableProperty() = apply { this.nullableProperty = null }
}
