package clientsgo

import kotlinx.coroutines.runBlocking
import settings.Settings

fun main() {

    val auth = Authorization("mazaloff", Settings.passwordGit)
    val deferred = ApiClientImp.loginAsync(auth)

    runBlocking {
        try {
            println(deferred.await())
        } catch (e: RuntimeException) {
            println("404")
        }
    }

}
