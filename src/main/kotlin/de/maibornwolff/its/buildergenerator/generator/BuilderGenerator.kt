package de.maibornwolff.its.buildergenerator.generator

import com.intellij.psi.PsiClass
import com.squareup.kotlinpoet.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtImportDirective

class BuilderGenerator(private val config: GeneratorConfig) {

    fun generateBuilderForDataClass(builtClass: KtClass): FileSpec {

        val builderClassName = builtClass.name + config.builderClassSuffix

        val dataClassFqName = builtClass.fqName!!.asString()
        val dataClassSimpleName = builtClass.name!!
        val packageName = dataClassFqName.getPathFromFqName()

        val knownTypeNameToPackageMap =
            generateImportedTypesMap(builtClass.containingKtFile.importDirectives) +
                    generateClassesInFileMap(
                        classes = builtClass.containingKtFile.classes
                            .filterNot { it.name!!.endsWith("Kt") },
                        packageName = packageName
                                            )

        val properties = builtClass.primaryConstructorParameters
            .map { Property.fromKtParameter(it, knownTypeNameToPackageMap) }

        return FileSpec.builder(packageName, builderClassName)
            .addImports(knownTypeNameToPackageMap)
            .addType(
                TypeSpec.classBuilder(ClassName(packageName, builderClassName))
                    .addPropertyFields(properties)
                    .addBuildFunction(properties, dataClassSimpleName)
                    .addWithFunctions(properties)
                    .build()
                    )
            .build()
    }

    private fun String.getPathFromFqName() = this.substring(0, this.lastIndexOf('.'))

    private fun generateImportedTypesMap(importDirectives: List<KtImportDirective>) =
        importDirectives
            .mapNotNull { importDirective ->
                importDirective.importPath?.let { path ->
                    path.importedName!!.asString() to path.fqName.asString().getPathFromFqName()
                }
            }
            .toMap()

    private fun generateClassesInFileMap(classes: Collection<PsiClass>, packageName: String) =
        classes.map { it.name!! to packageName }.toMap()

    private fun FileSpec.Builder.addImports(imports: Map<String, String>) = this.apply {
        imports.forEach { (name, path) ->
            this.addImport(path, name)
        }
    }

    private fun TypeSpec.Builder.addPropertyFields(properties: List<Property>) =
        this.addProperties(properties.map { property ->
            PropertySpec.builder(property.name, property.type.typeName)
                .addModifiers(KModifier.PRIVATE)
                .mutable()
                .initializer(CodeBlock.of(property.defaultValue))
                .build()
        })

    private fun TypeSpec.Builder.addWithFunctions(properties: List<Property>) =
        this.apply {
            properties.forEach {
                this.addWithFunction(it)
                if (it.type.isNullable) this.addWithoutFunction(it)
            }
        }

    private fun TypeSpec.Builder.addWithFunction(property: Property): TypeSpec.Builder {
        val nonNullableTypeName = property.type.typeName.copy(nullable = false)
        return this.addFunction(
            FunSpec.builder("${config.withFunctionPrefix}${property.name.capitalize()}")
                .addParameter(property.name, nonNullableTypeName)
                .addStatement("return apply { this.${property.name} = ${property.name} }")
                .build()
                               )
    }

    private fun TypeSpec.Builder.addWithoutFunction(property: Property): TypeSpec.Builder {
        return this.addFunction(
            FunSpec.builder("${config.withoutFunctionPrefix}${property.name.capitalize()}")
                .addStatement("return apply { this.${property.name} = null }")
                .build()
                               )
    }

    private fun TypeSpec.Builder.addBuildFunction(
        parameters: List<Property>,
        builtClassSimpleName: String
                                                 ): TypeSpec.Builder {
        return this.addFunction(
            FunSpec.builder(config.buildFunctionName)
                .addStatement("return ${builtClassSimpleName}(${parameters.joinToString(separator = ",\n") { "${it.name} = ${it.name}" }})")
                .build()
                               )
    }
}


