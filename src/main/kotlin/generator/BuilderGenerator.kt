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

    // TODO: return something useful to open generated class in intellij after action
    fun generateBuilderForDataClass(dataClass: KtClass): FileSpec {

        val builderClassName = dataClass.name + "Builder"

        val parameters = dataClass.primaryConstructorParameters
            .map { it.name!! to resolveType(it) }

        val dataClassFqName = dataClass.fqName!!.asString()
        val dataClassSimpleName = dataClass.name!!

        val packageName =  dataClassFqName.substring(0, dataClassFqName.lastIndexOf('.'))

        val file = FileSpec.builder(packageName, builderClassName)
            .addType(
                TypeSpec.classBuilder(ClassName(packageName, builderClassName))
                    .addPropertyFields(parameters)
                    .addBuildFunction(parameters, dataClassSimpleName, packageName)
                    .addWithFunctions(parameters, builderClassName, packageName)
                    .build()
                    )
            .build()

        return file
    }

    private fun resolveType(param: KtParameter): KClass<out Any> {
        val typeName = (param.typeReference?.typeElement as? KtUserType)?.referencedName
        val type = supportedTypesMap[typeName]
        return type ?: throw NotImplementedError("Parameter type $typeName not yet supported")
    }

    fun TypeSpec.Builder.addWithFunctions(
        parameters: List<Pair<String, KClass<out Any>>>,
        builderClassName: String,
        packageName: String
    ): TypeSpec.Builder {
        return this.apply {
            parameters.forEach {
                this.addWithFunction(it, builderClassName, packageName)
            }
        }
    }

    private fun TypeSpec.Builder.addWithFunction(
        it: Pair<String, KClass<out Any>>,
        builderClassName: String,
        packageName: String
    ): TypeSpec.Builder {
        val (name, type) = it

        return this.addFunction(FunSpec.builder("with${name.capitalize()}")
            .returns(ClassName(packageName, builderClassName))
            .addParameter(name, type)
            .addStatement("return apply { this.$name = $name }")
            .build())
    }

    fun TypeSpec.Builder.addBuildFunction(
        parameters: List<Pair<String, KClass<out Any>>>,
        dataClassSimpleName: String,
        packageName: String
    ): TypeSpec.Builder {
        return this.addFunction(FunSpec.builder("build")
            .returns(ClassName(packageName, dataClassSimpleName))
            .addStatement("return ${dataClassSimpleName}(${parameters.joinToString { (name, _) -> "$name = $name" }})")
            .build())
    }

    fun TypeSpec.Builder.addPropertyFields(parameters: List<Pair<String, KClass<out Any>>>) =
        this.addProperties(parameters.map { (name, kclass) ->
            PropertySpec.builder(name, kclass)
                .addModifiers(KModifier.PRIVATE)
                .mutable()
                .initializer(CodeBlock.of(defaultValuesMap[kclass]!!))
                .build()
        })
}
