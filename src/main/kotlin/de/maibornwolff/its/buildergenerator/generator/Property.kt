package de.maibornwolff.its.buildergenerator.generator

import org.jetbrains.kotlin.descriptors.ParameterDescriptor

data class Property(
    val name: String,
    val type: Type
                   ) {

    val defaultValue = when {
        type.isNullable -> "null"
        else            -> defaultValuesMap[type.simpleName] ?: "TODO(\"Needs a default value!\")"
    }

    companion object {

        val defaultValuesMap = mapOf(
            "String" to "\"a string\"",
            "Int" to "42",
            "Boolean" to "false",
            "Long" to "23L",
            "List" to "emptyList()",
            "Map" to "emptyMap()",
            "Set" to "emptySet()",
            "Array" to "emptyArray()"
                                    )

        fun fromParameterDescriptor(param: ParameterDescriptor) =
            Property(
                name = param.name.asString(),
                type = Type.fromKotlinType(param.type)
                    )
                .takeIf { it.name != "<this>" }
    }
}

