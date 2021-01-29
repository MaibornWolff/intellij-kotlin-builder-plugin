package generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
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

    // TODO: return something useful to open generated class in intellij after action
    fun generateBuilderForDataClass(dataClass: KtClass) {

        val builderClassName = dataClass.name + "Builder"

        val parameters = dataClass.primaryConstructorParameters
            .map { it.name to resolveType(it) }

        val file = FileSpec.builder("", builderClassName)
            .addType(
                TypeSpec.classBuilder(builderClassName)
                    // TODO
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
}