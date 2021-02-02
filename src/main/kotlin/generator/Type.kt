package generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.utils.keysToMap

data class Type(
    val simpleName: String,
    val packageName: String,
    val isNullable: Boolean,
    val typeArguments: List<Type>
               ) {

    companion object {

        private val knownCollectionTypes = listOf("List", "Set", "Map", "Array")

        fun fromKtParameter(
            param: KtParameter,
            importedTypeNameToPackage: Map<String, String>
                           ) =
            fromTypeElement(param.typeReference?.typeElement,
                            importedTypeNameToPackage +
                                    knownCollectionTypes.keysToMap { "kotlin.collections" })

        private fun fromTypeElement(
            typeElement: KtTypeElement?,
            importedTypeNameToPackage: Map<String, String>
                                   ): Type {
            return when (typeElement) {
                is KtUserType -> fromUserType(typeElement, importedTypeNameToPackage)

                is KtNullableType -> fromTypeElement(
                    typeElement.innerType,
                    importedTypeNameToPackage
                                                    ).copy(isNullable = true)

                else -> throw NotImplementedError("unknown typeElement")
            }
        }

        private fun fromUserType(
            userType: KtUserType,
            importedTypeNameToPackage: Map<String, String>,
            isNullable: Boolean = false
                                ) =
            userType.referencedName?.let { referencedName ->
                Type(
                    simpleName = referencedName,
                    packageName = importedTypeNameToPackage[referencedName] ?: "kotlin",
                    isNullable = isNullable,
                    typeArguments = userType.typeArguments.mapNotNull { typeProjection ->
                        typeProjection.typeReference?.typeElement?.let {
                            fromTypeElement(it, importedTypeNameToPackage)
                        }
                    }
                    )
            }
                ?: throw NotImplementedError("ktUserType?.referencedName is null, wat do")
    }

    val typeName: TypeName =
        ClassName(packageName, simpleName)
            .let { className ->
                if (typeArguments.isEmpty()) className
                else className.parameterizedBy(typeArguments.map { it.typeName })
            }
            .copy(nullable = isNullable)
}