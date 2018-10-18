package koans

/**
 * First assignment
 *
 * Implement correctly methods and interfaces required for the code in `main` method to compile.
 *
 *  1. Implement `kotlin.Comparable<InvoicingPeriod>` that will compare the periods (by month/year time)
 *  2. Implement `inc()` operator in order to increment period by one month
 *  3. Lastly, implement simple validation using `init {}` intializer that will throw IllegalArgumentException when
 *     parameter month is not within range of <1, 12> and year is less than 1970 :)
 *
 *  Try to "think" in Kotlin and use as less constructs as needed (e.g. make us of type inference, lambdas, etc.)
 *
 *  You can use these parts of documentation:
 *  - https://kotlinlang.org/docs/reference/interfaces.html#implementing-interfaces
 *  - https://kotlinlang.org/docs/reference/operator-overloading.html#increments-and-decrements
 *  - https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/require.html#require
 *
 */

data class InvoicingPeriod(val month: Int, val year: Int)
data class Invoice(val price: Int, val paid: Boolean, val invoicingPeriod: InvoicingPeriod)
data class Customer(val name: String, val projects: List<Project>)
data class Project(val name: String, val invoices: List<Invoice>)

fun main(args: Array<String>) {
    println(InvoicingPeriod(1, 2018) < InvoicingPeriod(10, 2018))
    println(InvoicingPeriod(1, 2018) == InvoicingPeriod(1, 2018))
    println(InvoicingPeriod(10, 2018) > InvoicingPeriod(1, 2018))
    println(InvoicingPeriod(5, 2018) in InvoicingPeriod(1, 2018)..InvoicingPeriod(12, 2018))
    var start = InvoicingPeriod(12, 2018)
    println(++start == InvoicingPeriod(1, 2019))
    try {
        println(InvoicingPeriod(-5, 2018))
    } catch (illegalArgumentException: IllegalArgumentException) {
        println("You've done it right!")
    }
}