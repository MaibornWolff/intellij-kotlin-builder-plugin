package linemarker

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.*


class KafkaLineMarkerProvider: RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(element: PsiElement,
                                          result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>) {

        // Marker am Event
        if (element is KtClass && element.isKapaEvent()) {
            val usages = ReferencesSearch
                .search(element)
                .findAll()
                .mapNotNull { it.element }

            val usagesInApps = usages
                .filter { it.containingFile.virtualFile.canonicalPath?.endsWith("App.kt") ?: false }

            val methodsUsingElementAsParameterInApps = usagesInApps
                .mapNotNull { PsiTreeUtil.getParentOfType(it, KtCallExpression::class.java) }

            val consumers = methodsUsingElementAsParameterInApps
                .mapNotNull {PsiTreeUtil.findChildOfType(it, KtSimpleNameExpression::class.java)}

            element.createNavigateToConsumerLineMarker(consumers)?.also { result.add(it) }

            val methodsUsingElementAsParameter = usages
                .filterNot { (it.containingFile.virtualFile.canonicalPath?.endsWith("Test.kt") ?: false)
                        || (it.containingFile.virtualFile.canonicalPath?.endsWith("Tests.kt") ?: false)}
                .mapNotNull { PsiTreeUtil.getParentOfType(it, KtCallExpression::class.java) }

            val producers = methodsUsingElementAsParameter
                .mapNotNull {PsiTreeUtil.findChildOfType(it, KtSimpleNameExpression::class.java)}
                .filter { it.text?.endsWith("Producer") ?: false }

            element.createNavigateToProducerLineMarker(producers)?.also { result.add(it) }
        }

        // Marker am Consumer und Producer
        /*if (element is KtClassLiteralExpression) {
                val refs = ReferencesSearch
                    .search(element)
                    .findAll()
                    .filter { it is KtClass }
                    .map { it as KtClass }
                    .filter { it.linemarker.isKapaEvent() }

                element.linemarker.createNavigateToConsumedEventLineMarker(refs.map { it.originalElement })?.also { result.add(it) }
        }*/

        super.collectNavigationMarkers(element, result)
    }
}

private fun KtClass.createNavigateToProducerLineMarker(producers: List<PsiElement>): RelatedItemLineMarkerInfo<PsiElement>? {
    val builder: NavigationGutterIconBuilder<PsiElement> = NavigationGutterIconBuilder
        .create(AllIcons.Hierarchy.Supertypes)
        .setTargets(producers)
        .setTooltipText("Navigiere zur Producern")

    return this.getClassKeyword()?.let { builder.createLineMarkerInfo(it) }
}

private fun PsiElement.createNavigateToConsumedEventLineMarker(consumedEvent: List<PsiElement>): RelatedItemLineMarkerInfo<PsiElement>? {
    val builder: NavigationGutterIconBuilder<PsiElement> = NavigationGutterIconBuilder
        .create(AllIcons.Hierarchy.Supertypes)
        .setTargets(consumedEvent)
        .setNamer { it.containingFile.virtualFile.nameWithoutExtension }
        .setTooltipText("Navigiere zum referenzierten Event")

    return builder.createLineMarkerInfo(this)
}

private fun KtClass.createNavigateToConsumerLineMarker(consumers: List<PsiElement>): RelatedItemLineMarkerInfo<PsiElement>? {
    val builder: NavigationGutterIconBuilder<PsiElement> = NavigationGutterIconBuilder
        .create(AllIcons.Hierarchy.Subtypes)
        .setTargets(consumers)
        .setTooltipText("Navigiere zur Consumern")

    return this.getClassKeyword()?.let { builder.createLineMarkerInfo(it) }
}

private fun KtClass.isKapaEvent() = this.annotationEntries.any { it.shortName.toString() == "EreignisMetadaten" }

//TODO
private fun KtValueArgument.getReference() = PsiTreeUtil.findChildOfType(this, KtReferenceExpression::class.java)?.originalElement
