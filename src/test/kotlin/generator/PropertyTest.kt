package generator

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.maibornwolff.its.buildergenerator.generator.Property
import org.jetbrains.kotlin.psi.KtParameter
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class PropertyTest {

    @Test
    fun `fromKtParameter should throw NotImplementedError when KtParameter has no name`(){
        // arrange
        val param = mock<KtParameter>()
        val importedTypeNameToPackage = mutableMapOf<String, String>()
        whenever(param.name).thenReturn(null)

        // act & assert
        assertThrows<NotImplementedError> { Property.fromKtParameter(param, importedTypeNameToPackage) }
    }
}