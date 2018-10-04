import kotlin.math.absoluteValue
import kotlin.math.sign

// Extension function
fun Int.padZeros(length: Int) = "${if (this.sign < 0) "-" else ""}${this.absoluteValue.toString().padStart(length, '0')}"

// Data class
data class Time(val hours: Int, val minutes: Int) : Comparable<Time> {

    companion object {
        // Static methods visible in Java code (inter-op)
        @JvmStatic
        fun convertToTime(time: Number): Time {
            return when (time) {
                is Double -> {
                    // Smart casting
                    val fullHours = Math.floor(time).toInt()
                    Time(fullHours, ((time - fullHours) * 60).toInt())
                }
                is Float -> convertToTime(time.toDouble())
                is Int -> Time(time)
                else -> Time(time.toInt())
            }
        }
    }

    // null-safe check (elvis operator)
    constructor(hours: Int?)
            : this(hours ?: 0, 0)

    // Operator overloading by implementing compareTo
    override fun compareTo(other: Time): Int {
        return when {
            this.hours == other.hours -> this.minutes - other.minutes
            else -> this.hours - other.hours
        }
    }

    // Operator overloading
    operator fun minus(other: Time): Time {
        return Time(this.hours - other.hours, this.minutes - other.minutes)
    }

    override fun toString() = "${hours.padZeros(2)}:${minutes.padZeros(2)}"

}

class Workday(override val start: Time, override val endInclusive: Time) : ClosedRange<Time> {

    companion object {
        @JvmStatic
        val defaultWorkingHours = 8
    }

    constructor() : this(Time(8), Time(16))

    init {
        // higher order function
        require(isValid()) { "Range `${this.start} to ${this.endInclusive}` does not define a valid workday." }
    }

    fun calculateOvertime(): Time {
        return endInclusive - start - Time(defaultWorkingHours)
    }

    fun isValid() = start >= Time(0, 0) && endInclusive <= Time(23, 59)
}

fun main(args: Array<String>) {
    println(Time(10, 30) in Workday())
    println(Time(7) in Workday())

    println(Workday(Time(8), Time(17, 15)).calculateOvertime())
    println(Workday(Time(8), Time(15)).calculateOvertime())

    println(Time.convertToTime(3.5))

    Workday(Time(-1), Time(12))
}