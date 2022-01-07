package testdata

import testdata.otherpackage.PropertyTypeWithBuilderBuilder

public class DataClassWithComplexTypeWithBuilderBuilder {

    private var property1: PropertyTypeWithBuilder = PropertyTypeWithBuilderBuilder().build()

    public fun build() = DataClassWithComplexTypeWithBuilder(property1 = property1)

    public fun withProperty1(property1: PropertyTypeWithBuilder) = apply {
        this.property1 = property1
    }

}