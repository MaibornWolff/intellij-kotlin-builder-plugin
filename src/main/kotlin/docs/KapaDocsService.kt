package docs

import com.intellij.openapi.fileEditor.FileEditorManagerEvent

class KapaDocsService {
    var kapaDocsTopicDependenciesToolWindow: KapaDocsToolWindow? = null
    var kapaDocsToolWindow: KapaDocsToolWindow? = null

    fun onFileEditorSelectionChanged(event: FileEditorManagerEvent) {
        if(event.newFile != null && event.newFile?.path?.contains("src/main/kotlin/") == true) {
            val kapaComponent = event.newFile?.path
                    ?.replace("\\", "/")
                    ?.replaceBefore("src/main/kotlin/", "")
                    ?.replace("src/main/kotlin/", "")
                    ?.split("/")
                    ?.dropLast(1)
                    ?.take(2)
                    ?.joinToString(".")

            if (kapaComponent != null) {
                val url = "https://something.tld/topicdependencies.html${"#$kapaComponent"}"
                kapaDocsTopicDependenciesToolWindow?.onUrlChanged(url)
            } else {
                val url = "https://something.tld/topicdependencies.html"
                kapaDocsTopicDependenciesToolWindow?.onUrlChanged(url)
            }
        } else {
            val url = "https://something.tld/topicdependencies.html"
            kapaDocsTopicDependenciesToolWindow?.onUrlChanged(url)
        }
    }
}
