package docs

import com.intellij.openapi.wm.ToolWindow
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView
import java.net.Authenticator

class KapaDocsToolWindow(private val toolWindow: ToolWindow) {

    val fxPanel = JFXPanel()

    init {
        Platform.setImplicitExit(false)
        toolWindow.component.parent.add(fxPanel)
    }

    fun onUrlChanged(url: String) {
        Platform.runLater {
            fxPanel.scene = this.makeScene(url)
        }
    }

    private fun makeScene(url: String): Scene {
        Authenticator.setDefault(KapaAuthenticator())
        val wv = WebView()
        wv.engine.load(url)
        return Scene(wv)
    }
}
