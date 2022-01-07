package de.maibornwolff.its.buildergenerator.generator

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName

data class Property(val name: String,
                    val type: Type) {

    fun getDefaultValue(project: Project,
                        config: GeneratorConfig): CodeBlock {
        val primitiveDefault = defaultValuesMap[type.simpleName]
        return when {
            type.isNullable                   -> CodeBlock.of("null")
            primitiveDefault != null          -> CodeBlock.of(primitiveDefault)
            type.wrappedPrimitiveType != null -> generateWrappedPrimitiveDefault(type.wrappedPrimitiveType)
            else                              -> generateDefaultForComplexType(project, config)
        }
    }

    private fun generateDefaultForComplexType(project: Project,
                                              config: GeneratorConfig) =
        generateDefaultFromBuilder(project, config) ?:
        CodeBlock.of("TODO(\"Needs·a·default·value!\")")

    private fun generateDefaultFromBuilder(project: Project,
                                           config: GeneratorConfig): CodeBlock? {
        val expectedBuilderClassName = "${type.simpleName}${config.builderClassSuffix}"
        val propertyBuilderClass: PsiClass? = PsiShortNamesCache
                .getInstance(project)
                .getClassesByName(expectedBuilderClassName, GlobalSearchScope.allScope(project)).singleOrNull()
        val propertyBuilderPackageName = propertyBuilderClass?.getKotlinFqName()?.parent()?.toString()
        val propertyBuilderName = propertyBuilderClass?.name
        return if (propertyBuilderPackageName != null && propertyBuilderName != null) {
            CodeBlock.Builder()
                    .add("%T", ClassName(propertyBuilderPackageName, propertyBuilderName))
                    .add("().${config.buildFunctionName}()")
                    .build()
        } else null
    }

    private fun generateWrappedPrimitiveDefault(wrappedType: WrappedPrimitive): CodeBlock {
        val wrappedTypeName = wrappedType.simpleName
        val wrappingTypeName = type.simpleName
        return CodeBlock.of("$wrappingTypeName(${defaultValuesMap[wrappedTypeName]})")
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

