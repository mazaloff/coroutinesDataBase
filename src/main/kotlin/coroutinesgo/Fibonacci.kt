package coroutinesgo

// FIBONACCI NUMBERS

object Fibonacci {

    private val fib = sequence<Long> {
        var cur = 1L
        var next = 1L
        while (true) {
            yield(next)
            val tmp = cur + next
            cur = next
            next = tmp
        }
    }

    fun run(number: Int): Long {
        return fib.take(number).last()
    }
}
