package testdata

data class DataClassWithComplexTypes(val property1: PropertyType1,
                                     val property2: PropertyType2,
                                     val listProperty: List<String>,
                                     val mapOfComplexTypeProperty: Map<Int, PropertyType2>,
                                     val nullableProperty: String?)

data class PropertyType1(val rawValue: Int, val anotherRawValue: Int)

data class PropertyType2(val rawValue: String, val otherValue: Boolean)