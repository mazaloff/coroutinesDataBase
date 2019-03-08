package coroutinesgo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// PIPELINE CHANNELS

@ExperimentalCoroutinesApi
object Channels {

    fun launchChannels() = runBlocking {
        val numbers = produceInt()
        val channelsTest = produceTest(numbers)
        repeat(10) { launchProcessor(it, channelsTest) }
    }

    private fun CoroutineScope.produceInt() = produce {
        repeat(10000) { send(it) }
    }

    private fun CoroutineScope.produceTest(numbers: ReceiveChannel<Int>): ReceiveChannel<Test> = produce {
        for (x in numbers) send(Test(x))
    }

    private fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Test>) = launch {
        for (entity in channel) {
            entity.name = "Processor #$id received $entity"
            Listener.onInvoke(entity)
            //println("Processor #$id received $entity")
        }
    }

}