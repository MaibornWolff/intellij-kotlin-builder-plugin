package docs

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener

class KapaDocsFileEditorListener(): FileEditorManagerListener {

    override fun selectionChanged(event: FileEditorManagerEvent) {
        val kapaDocsService: KapaDocsService =
                ServiceManager.getService(event.manager.project,
                                          KapaDocsService::class.java)
        kapaDocsService.onFileEditorSelectionChanged(event)
        super.selectionChanged(event)
    }

}
