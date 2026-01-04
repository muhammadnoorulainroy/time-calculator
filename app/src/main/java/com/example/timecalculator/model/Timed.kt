package com.example.timecalculator.model

// Generic wrapper that combines a Duration with any value
// Used for creating timed tasks (todos)
data class Timed<T>(
    val duration: Duration,
    val value: T
) {
    // Transform the value while keeping the same duration
    fun <U> map(f: (T) -> U): Timed<U> =
        Timed(duration, f(value))

    // Chain operations and accumulate durations
    fun <U> flatMap(f: (T) -> Timed<U>): Timed<U> {
        val next = f(value)
        return Timed(
            duration = duration + next.duration,
            value = next.value
        )
    }

    companion object {
        // Wrap a value with zero duration
        fun <T> pure(value: T): Timed<T> =
            Timed(Duration.ZERO, value)

        // Combine list of Timed items into single Timed with accumulated duration
        fun <T> combine(items: List<Timed<T>>): Timed<List<T>> =
            items.fold(pure(emptyList())) { acc, timed ->
                acc.flatMap { list ->
                    timed.map { value -> list + value }
                }
            }
    }
}

// Type alias for Todo - a timed string description
typealias Todo = Timed<String>
