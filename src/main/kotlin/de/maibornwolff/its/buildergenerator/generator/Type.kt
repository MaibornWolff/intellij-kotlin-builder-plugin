package de.maibornwolff.its.buildergenerator.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.types.KotlinType

data class Type(val simpleName: String,
                val packageName: String,
                val isNullable: Boolean,
                val wrappedPrimitiveType: WrappedPrimitive?,
                val typeArguments: List<Type>) {

    companion object {

        fun fromKotlinType(type: KotlinType): Type {
            val simpleName = type.fqName?.shortName()?.asString()
                    ?: throw NotImplementedError("Type has no FQ name: $type")
            val packageName = type.fqName?.parent()?.toString() ?: ""

            return Type(simpleName = simpleName,
                        packageName = packageName,
                        isNullable = type.isMarkedNullable,
                        wrappedPrimitiveType = WrappedPrimitive.fromKotlinType(type),
                        typeArguments = type.arguments.map { fromKotlinType(it.type) })
        }
    }

    val typeName: TypeName =
            ClassName(packageName, simpleName)
                    .let { className ->
                        if (typeArguments.isEmpty()) className
                        else className.parameterizedBy(typeArguments.map { it.typeName })
                    }
                    .copy(nullable = isNullable)
}