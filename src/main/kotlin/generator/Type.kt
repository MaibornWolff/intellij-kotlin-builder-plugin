package generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtUserType

data class Type(
    val simpleName: String,
    val packageName: String,
    val isNullable: Boolean
               ) {

    companion object {

        fun fromKtParameter(
            param: KtParameter,
            importedTypeNameToPackage: Map<String, String>
                           ): Type {

            val typeElement = param.typeReference?.typeElement

            return when (typeElement) {
                is KtUserType     -> fromUserType(typeElement, importedTypeNameToPackage)

                is KtNullableType -> (typeElement.innerType as? KtUserType)
                    ?.let { fromUserType(it, importedTypeNameToPackage, isNullable = true) }
                    ?: throw NotImplementedError("nullable type contains something else than KtUserType, wat do")

                else              -> throw NotImplementedError("unknown typeElement")
            }
        }

        private fun fromUserType(
            userType: KtUserType,
            importedTypeNameToPackage: Map<String, String>,
            isNullable: Boolean = false
                                ) =
            userType.referencedName?.let {
                Type(
                    simpleName = it,
                    packageName = importedTypeNameToPackage[it] ?: "kotlin",
                    isNullable = isNullable
                    )
            }
                ?: throw NotImplementedError("ktUserType?.referencedName is null, wat do")
    }

    val typeName: TypeName = ClassName(packageName, simpleName).copy(nullable = isNullable)
}