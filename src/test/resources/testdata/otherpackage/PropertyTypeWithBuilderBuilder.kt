package testdata.otherpackage

import testdata.PropertyTypeWithBuilder
import kotlin.Boolean
import kotlin.Int

public class PropertyTypeWithBuilderBuilder {

    private var rawValueInt: Int = 42
    private var rawValueBoolean: Boolean = false

    public fun build() = PropertyTypeWithBuilder(rawValueInt, rawValueBoolean)
}
