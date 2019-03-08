package coroutinesgo

import khttp.get
import khttp.post
import kotlinx.coroutines.*
import model.Widgets
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import service.DatabaseFactory
import kotlin.collections.set

@ExperimentalCoroutinesApi
fun main() {

    DatabaseFactory.init(false)
    transaction {
        Widgets.deleteAll()
    }

    val promise = launchTestCoroutines()
    //Channels.launchChannels()

    val respond1 = get("http://127.0.0.1:8080/widget/")
    println(respond1.statusCode)

    val respond2 = post(
        "http://127.0.0.1:8080/widget/", mapOf(
            "Content-Type" to "application/json",
            "Connection" to "close"
        ), json = mapOf("id" to "3", "name" to "name3", "quantity" to "3")
    )
    println(respond2.statusCode)

    runBlocking {
        promise.join()
    }

}

// LAUNCH COROUTINES

fun launchTestCoroutines() = GlobalScope.launch {

    val mapJobs = mutableMapOf<Int, Job>()

    repeat(10) { time ->
        val job = launch(Dispatchers.IO) {
            //CoroutineScope(newSingleThreadContext("thread$time"))
            repeat(1000) { i ->
                Listener.onInvoke(Test(time * 1000 + i, "received ${time * 1000 + i}")).also {
                    println("[${Thread.currentThread().name}] product ${time * 1000 + i}")
                }
            }
        }
        mapJobs[time] = job
    }

}


