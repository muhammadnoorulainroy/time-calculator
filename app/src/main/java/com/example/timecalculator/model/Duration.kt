package com.example.timecalculator.model

// Immutable Duration class that stores time in days, hours, and minutes
// Uses private constructor to ensure all values are normalized
data class Duration private constructor(
    val days: Int,
    val hours: Int,
    val minutes: Int
) {
    // Convert duration to total minutes for calculations
    fun toTotalMinutes(): Int =
        days * MINUTES_PER_DAY + hours * MINUTES_PER_HOUR + minutes

    // Operator overloading - allows using + to add two Durations
    operator fun plus(other: Duration): Duration =
        fromTotalMinutes(this.toTotalMinutes() + other.toTotalMinutes())

    // Custom string representation like "1d 2h 30m"
    override fun toString(): String {
        val parts = mutableListOf<String>()
        if (days > 0) parts.add("${days}d")
        if (hours > 0) parts.add("${hours}h")
        if (minutes > 0 || parts.isEmpty()) parts.add("${minutes}m")
        return parts.joinToString(" ")
    }

    companion object {
        private const val MINUTES_PER_HOUR = 60
        private const val HOURS_PER_DAY = 24
        private const val MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY

        // Zero duration constant
        val ZERO = Duration(0, 0, 0)

        // Factory method to create Duration with automatic normalization
        fun of(days: Int = 0, hours: Int = 0, minutes: Int = 0): Duration {
            val totalMinutes = days * MINUTES_PER_DAY + hours * MINUTES_PER_HOUR + minutes
            return fromTotalMinutes(totalMinutes)
        }

        // Creates normalized Duration from total minutes
        fun fromTotalMinutes(totalMinutes: Int): Duration {
            val d = totalMinutes / MINUTES_PER_DAY
            val remainingAfterDays = totalMinutes % MINUTES_PER_DAY
            val h = remainingAfterDays / MINUTES_PER_HOUR
            val m = remainingAfterDays % MINUTES_PER_HOUR
            return Duration(d, h, m)
        }
    }
}
