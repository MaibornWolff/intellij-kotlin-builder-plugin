package de.maibornwolff.its.buildergenerator.generator

import org.jetbrains.kotlin.psi.KtParameter

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

        fun fromKtParameter(
            param: KtParameter,
            importedTypeNameToPackage: Map<String, String>
                           ) =
            Property(
                name = param.name ?: throw NotImplementedError("parameter has no name, wat do"),
                type = Type.fromKtParameter(param, importedTypeNameToPackage)
                    )
    }
}

