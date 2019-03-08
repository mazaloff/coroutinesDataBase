package clientsgo

import settings.Settings
import kotlinx.coroutines.runBlocking

fun main() {

    val auth = Authorization("mazaloff", Settings.passwordGit)

    val deferred = ApiClientImp.login(auth)

    runBlocking {
        try {
            println(deferred.await())
        }catch (e:RuntimeException) {
            println("404")
        }
    }

}
