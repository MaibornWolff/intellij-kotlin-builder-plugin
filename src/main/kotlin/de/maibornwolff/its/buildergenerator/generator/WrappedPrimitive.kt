package de.maibornwolff.its.buildergenerator.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.js.descriptorUtils.nameIfStandardType
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassMemberScope
import org.jetbrains.kotlin.types.KotlinType

data class WrappedPrimitive(val packageName: String,
                            val simpleName: String,
                            val isNullable: Boolean) {

    companion object {

        fun fromKotlinType(type: KotlinType): WrappedPrimitive? {
            val typeMemberScope = type.memberScope
            return if (typeMemberScope is LazyClassMemberScope) {
                val singleConstructor = typeMemberScope.getConstructors().singleOrNull()
                val singleParameterType = singleConstructor?.valueParameters?.singleOrNull()?.type
                createWrappedPrimitiveFromSingleConstructorParameter(singleParameterType)
            } else {
                null
            }
        }

        private fun createWrappedPrimitiveFromSingleConstructorParameter(singleParameterType: KotlinType?): WrappedPrimitive? {
            val typeNameOfSingleParameter = singleParameterType?.nameIfStandardType?.toString()
            val packageName = singleParameterType?.fqName?.parent().toString()
            val wrappedPrimitiveTypeName =
                    typeNameOfSingleParameter?.takeIf { Property.primitiveDefaultValuesMap.containsKey(it) }
            val nullablePrimitive = singleParameterType?.isMarkedNullable
            return if (nullablePrimitive != null && wrappedPrimitiveTypeName != null) {
                WrappedPrimitive(packageName, wrappedPrimitiveTypeName, nullablePrimitive)
            } else null
        }
    }

    val typeName: TypeName =
            ClassName(packageName, simpleName)
                    .copy(nullable = isNullable)
}
