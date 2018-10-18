package koans

/**
 * Implement these methods for lists of customers.
 */

/**
 * This function returns the sum of paid invoices for customer and month.
 *
 * Return a Map, where key is a customer name and the value is the sum.
 */
fun List<Customer>.calculateRevenuePerCustomerAndMonth(month: Int): Map<String, Int> = TODO()

/**
 * This function returns the project, which has the highest sum of all paid invoices
 */
fun List<Customer>.findMostProfitableProject(): Project? = TODO()


class TestData {
    companion object {
        val customers = listOf(
            Customer("Westside Chocolate Factory", listOf(
                Project("Chocolate Quality Improvement", listOf(
                    Invoice(14_000_000, false, InvoicingPeriod(10, 2018)),
                    Invoice(13_000_000, true, InvoicingPeriod(9, 2018)),
                    Invoice(10_000_000, true, InvoicingPeriod(8, 2018)),
                    Invoice(4_000_000, true, InvoicingPeriod(7, 2018))
                )),
                Project("Unnamed Hot Cocoa Project", listOf(
                    Invoice(45_000_000, false, InvoicingPeriod(10, 2018)),
                    Invoice(40_000_000, false, InvoicingPeriod(9, 2018))
                ))
            )),
            Customer("Granny's little bakery", listOf(
                Project("European Market Expansion", listOf(
                    Invoice(314_000_000, false, InvoicingPeriod(10, 2018)),
                    Invoice(300_000_000, true, InvoicingPeriod(9, 2018)),
                    Invoice(430_000_000, true, InvoicingPeriod(8, 2018))
                ))
            )),
            Customer("Arnie's Pizza Palace", listOf(
                Project("Oven maintenance", listOf(
                    Invoice(13_000, true, InvoicingPeriod(10, 2018)),
                    Invoice(11_000, true, InvoicingPeriod(9, 2018)),
                    Invoice(13_000, true, InvoicingPeriod(8, 2018))
                )),
                Project("Delivery car fleet upgrade", listOf(
                    Invoice(250_000, false, InvoicingPeriod(10, 2018)),
                    Invoice(450_000, true, InvoicingPeriod(9, 2018)),
                    Invoice(133_000, true, InvoicingPeriod(8, 2018))
                ))
            ))
        )
    }
}

fun main(args: Array<String>) {
    println(0 == TestData.customers.calculateRevenuePerCustomerAndMonth(10)["Westside Chocolate Factory"])
    println(13_000_000 == TestData.customers.calculateRevenuePerCustomerAndMonth(9)["Westside Chocolate Factory"])
    println(10_000_000 == TestData.customers.calculateRevenuePerCustomerAndMonth(8)["Westside Chocolate Factory"])
    println(11_000 + 450_000 == TestData.customers.calculateRevenuePerCustomerAndMonth(9)["Arnie's Pizza Palace"])
    println(430_000_000 == TestData.customers.calculateRevenuePerCustomerAndMonth(8)["Granny's little bakery"])
    println("European Market Expansion" == TestData.customers.findMostProfitableProject()!!.name)
}