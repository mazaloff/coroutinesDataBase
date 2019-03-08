package coroutinesgo

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// GENERATE SEQUENCE

object Generator {

    fun run() = runBlocking {

        val channel = Channel<String?>()

        launch {
            for (i in 'A'..'X') {
                channel.send(i.toString())
                delay(400)
            }
            channel.close()
        }

        launch {
            for (i in channel) {
                println(i)
            }
            println("finished")
        }

    }

}
