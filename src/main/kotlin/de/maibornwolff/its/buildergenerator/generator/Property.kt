package de.maibornwolff.its.buildergenerator.generator

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import org.jetbrains.kotlin.descriptors.ParameterDescriptor

data class Property(val name: String,
                    val type: Type) {

    fun getDefaultValue(project: Project,
                        config: GeneratorConfig): String = when {
        type.isNullable                   -> "null"
        type.wrappedPrimitiveType != null -> generateWrappedPrimitiveDefault(type.wrappedPrimitiveType)
        else                              -> defaultValuesMap[type.simpleName] ?:
                                                generateDefaultFromBuilder(project, config) ?:
                                                "TODO(\"Needs·a·default·value!\")"
    }

    private fun generateDefaultFromBuilder(project: Project,
                                           config: GeneratorConfig): String? {
        val expectedBuilderClassName = "${type.simpleName}${config.builderClassSuffix}"
        val classByName = PsiShortNamesCache
                .getInstance(project)
                .getClassesByName(expectedBuilderClassName, GlobalSearchScope.allScope(project)).singleOrNull()
        return if (classByName != null) {
            "${classByName.name}().${config.buildFunctionName}()"
        } else null
        // TODO wie den import hinbekommen ?
    }

    private fun generateWrappedPrimitiveDefault(wrappedType: WrappedPrimitive): String {
        val wrappedTypeName = wrappedType.simpleName
        val wrappingTypeName = type.simpleName
        return "$wrappingTypeName(${defaultValuesMap[wrappedTypeName]})"
    }

    companion object {

        private val collectionsDefaultValuesMap = mapOf("List" to "emptyList()",
                                                        "Map" to "emptyMap()",
                                                        "Set" to "emptySet()",
                                                        "Array" to "emptyArray()")

        val primitiveDefaultValuesMap = mapOf("String" to "\"a·string\"",
                                              "Float" to "1.0F",
                                              "Double" to "1.0",
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

