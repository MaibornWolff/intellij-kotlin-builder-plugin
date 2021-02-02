package generator

import com.squareup.kotlinpoet.ClassName
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

            val ktUserType = param.typeReference?.typeElement as? KtUserType

            return ktUserType?.referencedName?.let {
                Type(
                    simpleName = it,
                    packageName = importedTypeNameToPackage[it] ?: "kotlin",
                    isNullable = false // TODO find out if type is nullable
                    )
            }
                ?: throw NotImplementedError("ktUserType?.referencedName is null, wat do")
        }
    }

    val className = ClassName(packageName, simpleName)
}