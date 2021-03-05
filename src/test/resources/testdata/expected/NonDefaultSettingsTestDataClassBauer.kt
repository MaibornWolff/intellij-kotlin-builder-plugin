package testdata

import kotlin.Int

public class NonDefaultSettingsTestDataClassBauer {

    private var nullableProperty: Int? = null

    public fun baue() = NonDefaultSettingsTestDataClass(nullableProperty = nullableProperty)

    public fun mitNullableProperty(nullableProperty: Int) = apply {
        this.nullableProperty =
            nullableProperty
    }

    public fun ohneNullableProperty() = apply { this.nullableProperty = null }
}
