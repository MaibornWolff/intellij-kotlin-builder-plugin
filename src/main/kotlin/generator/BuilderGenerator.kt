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
    fun generateBuilderForDataClass(dataClass: KtClass) {

        val builderClassName = dataClass.name + "Builder"

        val parameters = dataClass.primaryConstructorParameters
            .map { it.name!! to resolveType(it) }

        val file = FileSpec.builder("", builderClassName)
            .addType(
                TypeSpec.classBuilder(builderClassName)
                    .addPropertyFields(parameters)
                    .addBuildFunction(dataClass, parameters)
                    // TODO: build function
                    .build()
                    )
            .build()

        file.writeTo(System.out)
    }

    private fun resolveType(param: KtParameter): KClass<out Any> {
        val typeName = (param.typeReference?.typeElement as? KtUserType)?.referencedName
        val type = supportedTypesMap[typeName]
        return type ?: throw NotImplementedError("Parameter type $typeName not yet supported")
    }

    fun TypeSpec.Builder.addBuildFunction(dataClass: KtClass, parameters: List<Pair<String, KClass<out Any>>>): TypeSpec.Builder {
        val fqName = dataClass.fqName!!.asString()
        val packageName =  fqName.substring(0, fqName.lastIndexOf('.'))
        val simpleName = dataClass.name!!
        return this.addFunction(FunSpec.builder("build")
            .returns(ClassName(packageName, simpleName))
            .addStatement("return ${simpleName}(${parameters.joinToString { (name, _) -> "$name = $name" }})")
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