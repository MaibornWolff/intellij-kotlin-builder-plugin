package generator

import com.intellij.psi.PsiClass
import com.squareup.kotlinpoet.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtImportDirective

object BuilderGenerator {

    fun generateBuilderForDataClass(dataClass: KtClass): FileSpec {

        val builderClassName = dataClass.name + "Builder"

        val dataClassFqName = dataClass.fqName!!.asString()
        val dataClassSimpleName = dataClass.name!!
        val packageName = dataClassFqName.getPathFromFqName()

        val knownTypeNameToPackageMap =
            generateImportedTypesMap(dataClass.containingKtFile.importDirectives) +
                    generateClassesInFileMap(dataClass.containingKtFile.classes
                                                 .filterNot { it.name!!.endsWith("Kt") }
                                                 .toTypedArray(),
                                             packageName)

        val properties =
            dataClass.primaryConstructorParameters.map { Property.fromKtParameter(it, knownTypeNameToPackageMap) }

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

    private fun generateClassesInFileMap(classes: Array<PsiClass>, packageName: String) =
        classes.map { it.name!! to packageName }.toMap()

    private fun FileSpec.Builder.addImports(imports: Map<String, String>) = this.apply {
        imports.forEach { (name, path) ->
            this.addImport(path, name)
        }
    }

    private fun TypeSpec.Builder.addPropertyFields(properties: List<Property>) =
        this.addProperties(properties.map { property ->
            PropertySpec.builder(property.name, property.type.className)
                .addModifiers(KModifier.PRIVATE)
                .mutable()
                .initializer(CodeBlock.of(property.defaultValue))
                .build()
        })

    private fun TypeSpec.Builder.addWithFunctions(properties: List<Property>) =
        this.apply {
            properties.forEach {
                this.addWithFunction(it)
            }
        }

    private fun TypeSpec.Builder.addWithFunction(property: Property): TypeSpec.Builder {
        return this.addFunction(
            FunSpec.builder("with${property.name.capitalize()}")
                .addParameter(property.name, property.type.className)
                .addStatement("return apply { this.${property.name} = ${property.name} }")
                .build()
                               )
    }

    private fun TypeSpec.Builder.addBuildFunction(
        parameters: List<Property>,
        dataClassSimpleName: String
                                                 ): TypeSpec.Builder {
        return this.addFunction(
            FunSpec.builder("build")
                .addStatement("return ${dataClassSimpleName}(${parameters.joinToString { "${it.name} = ${it.name}" }})")
                .build()
                               )
    }
}


