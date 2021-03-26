package de.maibornwolff.its.buildergenerator.actions

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TestDialog
import com.intellij.openapi.ui.TestDialogManager
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import com.intellij.testFramework.fixtures.JavaTestFixtureFactory
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.idea.core.moveCaret
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.File

@Ignore("WIP: This _almost_ works, but for some reason, the KtClass passed through Event.dataContext.psiElement " +
                "does not have a reference to its module (dataClass.module == null), and so the SourceRootChoice" +
                "skips over all the logic straight to line 45.")
class MultipleSourceRootTest: HeavyPlatformTestCase() {

    @Test
    fun test_PresentsSourceRootChoiceDialog_OverwritesExistingBuilderInTestSourceRootByDefault() {
        // arrange
        val inputFile = "main/SimpleDataClass.kt"
        val caretOffsetOnDataClassName = 40
        myFixture.configureByFile(inputFile)

        var overwriteDialogShown = false
        var sourceRootChoiceShown = false
        TestDialogManager.setTestDialog { message ->
            when (message) {

                "Target file 'SimpleDataClassBuilder.kt' already exists and will be overwritten. Continue?" ->
                    Messages.OK.also { overwriteDialogShown = true }

                "Choose target source root for the builder class:"                                          ->
                    Messages.OK.also { sourceRootChoiceShown = true }

                else                                                                                        ->
                    throw IllegalStateException("Unexpected message box: $message")
            }
        }

        // act
        myFixture.editor.moveCaret(caretOffsetOnDataClassName)
        myFixture.testAction(GenerateBuilderAction())

        // assert
        assertThat(sourceRootChoiceShown).isTrue

        assertThat(overwriteDialogShown).isTrue
    }

    private lateinit var myFixture: JavaCodeInsightTestFixture

    private val testDataPath: String =
            File(this::class.java.getResource("/testdata/SimpleDataClass.kt").toURI()).parent + "/multiroot"

    @Before
    public override fun setUp() {
        val projectBuilder = JavaTestFixtureFactory.createFixtureBuilder(name)
        myFixture = JavaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(projectBuilder.fixture)
        myFixture.testDataPath = testDataPath

        val projectBuilderWithModule = projectBuilder.addModule(JavaModuleFixtureBuilder::class.java)

        val moduleFixture = projectBuilderWithModule
                .addContentRoot(testDataPath)
                .addSourceRoot("main")
                .addSourceRoot("test")
                .fixture

        myFixture.setUp()
    }

    @After
    fun resetTestDialog() {
        TestDialogManager.setTestDialog(TestDialog.DEFAULT)
    }

    override fun tearDown() {
        super.tearDown()
        myFixture.tearDown()
    }
}