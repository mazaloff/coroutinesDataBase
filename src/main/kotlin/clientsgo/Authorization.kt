package clientsgo

import khttp.structures.authorization.Authorization
import java.util.*

data class Authorization(val user: String, val password: String): Authorization {
    override val header: Pair<String, String>
        get() {
            val b64 = Base64.getEncoder().encode("${this.user}:${this.password}".toByteArray()).toString(Charsets.UTF_8)
            return "Authorization" to "Basic $b64"
        }
}