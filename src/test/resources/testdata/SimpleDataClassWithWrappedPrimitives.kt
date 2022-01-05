package testdata

data class SimpleDataClassWithWrappedPrimitives(val intProperty: WrappedInt,
                                                val stringProperty: WrappedString,
                                                val booleanProperty: WrappedBoolean)

data class WrappedInt(val rawValue: Int)

data class WrappedString(val rawValue: String)

data class WrappedBoolean(val rawValue: Boolean)
