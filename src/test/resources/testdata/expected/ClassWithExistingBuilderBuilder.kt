package testdata

import kotlin.Int

public class ClassWithExistingBuilderBuilder {

    private var `property`: Int = 42

    public fun build() = ClassWithExistingBuilder(property = property)

    public fun withProperty(`property`: Int) = apply { this.property = property }
}