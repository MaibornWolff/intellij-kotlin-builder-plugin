package docs

import java.net.Authenticator
import java.net.PasswordAuthentication

// TODO Authentifizierung ordentlich verbauen
class KapaAuthenticator: Authenticator() {
    override fun getPasswordAuthentication() =
            PasswordAuthentication("TODO", "TODO".toCharArray())
}
