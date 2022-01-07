package de.maibornwolff.its.buildergenerator.generator

import com.intellij.openapi.project.Project
import com.squareup.kotlinpoet.*
import org.jetbrains.kotlin.backend.common.descriptors.allParameters
import org.jetbrains.kotlin.idea.caches.resolve.analyzeWithContent
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.resolve.BindingContext

class BuilderGenerator(private val config: GeneratorConfig) {

    fun generateBuilderForDataClass(builtClass: KtClass, project: Project): FileSpec {

        val bindingContext = builtClass.containingKtFile.analyzeWithContent()

        val builtClassDescriptor = bindingContext.get(BindingContext.CLASS, builtClass)
                ?: throw RuntimeException("Cannot get descriptor for the built class, wtf")

        val builderClassName = builtClass.name + config.builderClassSuffix
        val dataClassSimpleName = builtClass.fqName!!.shortName().asString()
        val packageName = builtClass.fqName!!.parent().asString()

        val properties = builtClassDescriptor.unsubstitutedPrimaryConstructor?.allParameters
                ?.mapNotNull { Property.fromParameterDescriptor(it) }
                ?: throw NotImplementedError("Class descriptor has no primary constructor for us to work with")

        return FileSpec.builder(packageName, builderClassName)
                .addType(
                        TypeSpec.classBuilder(ClassName(packageName, builderClassName))
                                .addPropertyFields(properties, project)
                                .addBuildFunction(properties, dataClassSimpleName)
                                .addWithFunctions(properties)
                                .build()
                )
                .build()
    }

    private fun TypeSpec.Builder.addPropertyFields(properties: List<Property>,
                                                   project: Project) =
            this.addProperties(properties.map { property ->
                PropertySpec.builder(property.name, property.type.typeName)
                        .addModifiers(KModifier.PRIVATE)
                        .mutable()
                        .initializer(property.getDefaultValue(project, config))
                        .build()
            })

    private fun TypeSpec.Builder.addWithFunctions(properties: List<Property>) =
            this.apply {
                properties.forEach {
                    this.addWithFunction(it)
                    this.addOverloadingWithFunctionForWrappedPrimitive(it)
                    if (it.type.isNullable) this.addWithoutFunction(it)
                }
            }

    private fun TypeSpec.Builder.addOverloadingWithFunctionForWrappedPrimitive(property: Property): TypeSpec.Builder {
        return if (property.type.wrappedPrimitiveType != null) {
            val wrappingTypeName = property.type.simpleName
            val nonNullableTypeName = property.type.wrappedPrimitiveType.typeName
            this.addFunction(
                    FunSpec.builder("${config.withFunctionPrefix}${property.name.capitalize()}")
                            .addParameter(property.name, nonNullableTypeName)
                            .addStatement("return·apply·{ this.${property.name}·= $wrappingTypeName(${property.name}) }")
                            .build()
            )
        } else this
    }

    private fun TypeSpec.Builder.addWithFunction(property: Property): TypeSpec.Builder {
        val nonNullableTypeName = property.type.typeName.copy(nullable = false)
        return this.addFunction(
                FunSpec.builder("${config.withFunctionPrefix}${property.name.capitalize()}")
                        .addParameter(property.name, nonNullableTypeName)
                        .addStatement("return·apply·{ this.${property.name}·= ${property.name} }")
                        .build()
        )
    }

    private fun TypeSpec.Builder.addWithoutFunction(property: Property): TypeSpec.Builder {
        return this.addFunction(
                FunSpec.builder("${config.withoutFunctionPrefix}${property.name.capitalize()}")
                        .addStatement("return·apply·{ this.${property.name}·= null }")
                        .build()
        )
    }

    private fun TypeSpec.Builder.addBuildFunction(
            parameters: List<Property>,
            builtClassSimpleName: String
    ): TypeSpec.Builder {
        return this.addFunction(
                FunSpec.builder(config.buildFunctionName)
                        .addStatement("return·${builtClassSimpleName}(${parameters.joinToString(separator = ",\n") { "${it.name}·= ${it.name}" }})")
                        .build()
        )
    }
}


