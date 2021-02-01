package generator

import com.intellij.psi.PsiClass
import com.squareup.kotlinpoet.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtUserType

object BuilderGenerator {

    // TODO if there already is a default value we should not replace it
    private val defaultValuesMap = mapOf(
        "String" to "\"a string\"",
        "Int" to "42",
        "Boolean" to "false",
        "Long" to "23L",
                                        )

    fun generateBuilderForDataClass(dataClass: KtClass): FileSpec {

        val builderClassName = dataClass.name + "Builder"

        val dataClassFqName = dataClass.fqName!!.asString()
        val dataClassSimpleName = dataClass.name!!
        val packageName = dataClassFqName.getPathFromFqName()

        val knownTypeNameToPackageMap = generateImportedTypesMap(dataClass.containingKtFile.importDirectives) +
                generateClassesInFileMap(dataClass.containingKtFile.classes, packageName)

        val parameters = dataClass.primaryConstructorParameters
            .map { it.name!! to resolveType(it, knownTypeNameToPackageMap) }

        return FileSpec.builder(packageName, builderClassName)
            .addImports(knownTypeNameToPackageMap)
            .addType(
                TypeSpec.classBuilder(ClassName(packageName, builderClassName))
                    .addPropertyFields(parameters)
                    .addBuildFunction(parameters, dataClassSimpleName)
                    .addWithFunctions(parameters)
                    .build()
                    )
            .build()
    }

    private fun String.getPathFromFqName() = this.substring(0, this.lastIndexOf('.'))

    private fun generateImportedTypesMap(importDirectives: List<KtImportDirective>) =
        importDirectives
            .mapNotNull { importDirective ->
                importDirective.importPath?.let { path ->
                    path.importedName!!.asString() to path.fqName.asString().getPathFromFqName() }
            }
            .toMap()

    private fun generateClassesInFileMap(classes: Array<PsiClass>, packageName: String) =
        classes.map { it.name!! to packageName }.toMap()

    private fun resolveType(param: KtParameter, importedTypeNameToPackage: Map<String, String>): ClassName {
        val ktUserType = param.typeReference?.typeElement as? KtUserType

        return ktUserType?.referencedName?.let { ClassName(importedTypeNameToPackage[it] ?: "kotlin", it) }
            ?: throw NotImplementedError("ktUserType?.referencedName cannot be null, wat do")
    }

    private fun FileSpec.Builder.addImports(imports: Map<String, String>) = this.apply {
        imports.forEach { (name, path) ->
            this.addImport(path, name)
        }
    }

    private fun TypeSpec.Builder.addPropertyFields(parameters: List<Pair<String, ClassName>>) =
        this.addProperties(parameters.map { (name, className) ->
            PropertySpec.builder(name, className)
                .addModifiers(KModifier.PRIVATE)
                .mutable()
                .initializer(CodeBlock.of(defaultValuesMap[className.simpleName] ?: "TODO(\"Needs a default value!\")"))
                .build()
        })

    private fun TypeSpec.Builder.addWithFunctions(parameters: List<Pair<String, ClassName>>) =
        this.apply {
            parameters.forEach {
                this.addWithFunction(it)
            }
        }

    private fun TypeSpec.Builder.addWithFunction(it: Pair<String, ClassName>): TypeSpec.Builder {
        val (name, type) = it

        return this.addFunction(
            FunSpec.builder("with${name.capitalize()}")
                .addParameter(name, type)
                .addStatement("return apply { this.$name = $name }")
                .build()
                               )
    }

    private fun TypeSpec.Builder.addBuildFunction(
        parameters: List<Pair<String, ClassName>>,
        dataClassSimpleName: String
                                                 ): TypeSpec.Builder {
        return this.addFunction(
            FunSpec.builder("build")
                .addStatement("return ${dataClassSimpleName}(${parameters.joinToString { (name, _) -> "$name = $name" }})")
                .build()
                               )
    }
}


