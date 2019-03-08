package coroutinesgo

import khttp.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Listener {

    private suspend fun postSuspend(it: Test) = withContext(Dispatchers.IO) {
        post("http://127.0.0.1:8080/widget/", mapOf(
                "Content-Type" to "application/json",
                "Connection" to "close"),
                json = mapOf("id" to it.id, "name" to it.name, "quantity" to it.id))
    }

    private val funcHttpPOST: suspend (Test) -> Unit = {
        postSuspend(it)
    }

    private fun init() {
        addListeners(0, funcHttpPOST)
    }

    private val listeners = mutableMapOf<Int, suspend (Test) -> Unit>()

    private fun addListeners(index: Int, value: suspend (Test) -> Unit) {
        listeners[index] = value
    }

    suspend fun onInvoke(entity: Test) {
        if (listeners.isEmpty()) init()
        listeners.values.forEach {
            it.invoke(entity)
        }
    }

}
