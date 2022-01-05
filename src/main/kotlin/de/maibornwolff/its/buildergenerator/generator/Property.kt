package de.maibornwolff.its.buildergenerator.generator

import org.jetbrains.kotlin.descriptors.ParameterDescriptor

data class Property(val name: String,
                    val type: Type) {

    val defaultValue: String = when {
        type.isNullable         -> "null"
        type.isWrappedPrimitive -> generateWrappedPrimitiveDefault()
        else                    -> defaultValuesMap[type.simpleName] ?: "TODO(\"Needs·a·default·value!\")"
    }

    private fun generateWrappedPrimitiveDefault(): String {
        val wrappedTypeName = type.wrappedPrimitiveType
        val wrappingTypeName = type.simpleName
        return "$wrappingTypeName(${defaultValuesMap[wrappedTypeName]})"
    }

    companion object {

        // TODO add defaults for Double, Float
        private val collectionsDefaultValuesMap = mapOf("List" to "emptyList()",
                                                        "Map" to "emptyMap()",
                                                        "Set" to "emptySet()",
                                                        "Array" to "emptyArray()")

        val primitiveDefaultValuesMap = mapOf("String" to "\"a·string\"",
                                              "Int" to "42",
                                              "Boolean" to "false",
                                              "Long" to "23L")

        val defaultValuesMap = primitiveDefaultValuesMap + collectionsDefaultValuesMap

        fun fromParameterDescriptor(param: ParameterDescriptor) =
                Property(name = param.name.asString(),
                         type = Type.fromKotlinType(param.type))
                        .takeIf { it.name != "<this>" }
    }
}

