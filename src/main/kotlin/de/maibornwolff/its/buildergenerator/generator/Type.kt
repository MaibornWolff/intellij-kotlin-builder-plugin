package de.maibornwolff.its.buildergenerator.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.js.descriptorUtils.nameIfStandardType
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassMemberScope
import org.jetbrains.kotlin.types.KotlinType

data class Type(val simpleName: String,
                val packageName: String,
                val isNullable: Boolean,
                val wrappedPrimitiveType: String?,
                val typeArguments: List<Type>) {

    val isWrappedPrimitive: Boolean
        get() = wrappedPrimitiveType != null

    companion object {

        fun fromKotlinType(type: KotlinType): Type {
            val simpleName = type.fqName?.shortName()?.asString()
                    ?: throw NotImplementedError("Type has no FQ name: $type")
            val packageName = type.fqName?.parent()?.toString() ?: ""

            return Type(simpleName = simpleName,
                        packageName = packageName,
                        isNullable = type.isMarkedNullable,
                        wrappedPrimitiveType = wrappedPrimitiveTypeName(type),
                        typeArguments = type.arguments.map { fromKotlinType(it.type) })
        }

        // TODO was passiert bei wrapped nullable primitive ?
        private fun wrappedPrimitiveTypeName(type: KotlinType): String? {
            val typeMemberScope = type.memberScope
            return if (typeMemberScope is LazyClassMemberScope) {
                val singleConstructor = typeMemberScope.getConstructors().singleOrNull()
                val singleParameter = singleConstructor?.valueParameters?.singleOrNull()
                val typeNameOfSingleParameter = singleParameter?.type?.nameIfStandardType?.toString()
                typeNameOfSingleParameter?.takeIf { Property.primitiveDefaultValuesMap.containsKey(it) }
            } else {
                null
            }
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