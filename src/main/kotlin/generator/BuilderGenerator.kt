package generator

import com.squareup.kotlinpoet.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtUserType
import kotlin.reflect.KClass

object BuilderGenerator {

    private val supportedTypesMap = mapOf(
        "String" to String::class,
        "Int" to Int::class,
        "Boolean" to Boolean::class
                                         )

    // TODO if there already is a default value we should not replace it
    private val defaultValuesMap = mapOf(
        String::class to "\"a string\"",
        Int::class to "42",
        Boolean::class to "false"
                                        )

    fun generateBuilderForDataClass(dataClass: KtClass): FileSpec {

        val builderClassName = dataClass.name + "Builder"

        val parameters = dataClass.primaryConstructorParameters
            .map { it.name!! to resolveType(it) }

        val dataClassFqName = dataClass.fqName!!.asString()
        val dataClassSimpleName = dataClass.name!!

        val packageName = dataClassFqName.substring(0, dataClassFqName.lastIndexOf('.'))

        return FileSpec.builder(packageName, builderClassName)
            .addType(
                TypeSpec.classBuilder(ClassName(packageName, builderClassName))
                    .addPropertyFields(parameters)
                    .addBuildFunction(parameters, dataClassSimpleName)
                    .addWithFunctions(parameters)
                    .build()
                    )
            .build()
    }

    private fun resolveType(param: KtParameter): KClass<out Any> {
        val typeName = (param.typeReference?.typeElement as? KtUserType)?.referencedName
        val type = supportedTypesMap[typeName]
        return type ?: throw NotImplementedError("Parameter type $typeName not yet supported")
    }

    private fun TypeSpec.Builder.addWithFunctions(parameters: List<Pair<String, KClass<out Any>>>) =
        this.apply {
            parameters.forEach {
                this.addWithFunction(it)
            }
        }

    private fun TypeSpec.Builder.addWithFunction(it: Pair<String, KClass<out Any>>): TypeSpec.Builder {
        val (name, type) = it

        return this.addFunction(
            FunSpec.builder("with${name.capitalize()}")
                .addParameter(name, type)
                .addStatement("return apply { this.$name = $name }")
                .build()
                               )
    }

    private fun TypeSpec.Builder.addBuildFunction(
        parameters: List<Pair<String, KClass<out Any>>>,
        dataClassSimpleName: String
                                                 ): TypeSpec.Builder {
        return this.addFunction(
            FunSpec.builder("build")
                .addStatement("return ${dataClassSimpleName}(${parameters.joinToString { (name, _) -> "$name = $name" }})")
                .build()
                               )
    }

    private fun TypeSpec.Builder.addPropertyFields(parameters: List<Pair<String, KClass<out Any>>>) =
        this.addProperties(parameters.map { (name, kclass) ->
            PropertySpec.builder(name, kclass)
                .addModifiers(KModifier.PRIVATE)
                .mutable()
                .initializer(CodeBlock.of(defaultValuesMap[kclass]!!))
                .build()
        })
}
