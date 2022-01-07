package de.maibornwolff.its.buildergenerator.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.js.descriptorUtils.nameIfStandardType
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassMemberScope
import org.jetbrains.kotlin.types.KotlinType

data class WrappedPrimitive(val packageName: String,
                            val simpleName: String) {

    companion object {

        fun fromKotlinType(type: KotlinType): WrappedPrimitive? {
            val typeMemberScope = type.memberScope
            return if (typeMemberScope is LazyClassMemberScope) {
                val singleConstructor = typeMemberScope.getConstructors().singleOrNull()
                val singleParameter = singleConstructor?.valueParameters?.singleOrNull()
                val typeNameOfSingleParameter = singleParameter?.type?.nameIfStandardType?.toString()
                val packageName = singleParameter?.type?.fqName?.parent().toString()
                val wrappedPrimitiveTypeName =
                        typeNameOfSingleParameter?.takeIf { Property.primitiveDefaultValuesMap.containsKey(it) }
                wrappedPrimitiveTypeName?.let { WrappedPrimitive(packageName, it) }
            } else {
                null
            }
        }
    }

    val typeName: TypeName =
            ClassName(packageName, simpleName)
                    .copy(nullable = false) // TODO
}
