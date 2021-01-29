package docs

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class KapaDocsToolWindowFactory(): ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val kapaDocsService: KapaDocsService = ServiceManager.getService(project, KapaDocsService::class.java)
        kapaDocsService.kapaDocsToolWindow = KapaDocsToolWindow(toolWindow)
        kapaDocsService.kapaDocsToolWindow?.onUrlChanged("https://something.tld")
    }

}

