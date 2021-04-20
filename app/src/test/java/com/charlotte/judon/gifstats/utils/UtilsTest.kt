package com.charlotte.judon.gifstats.utils

import com.charlotte.judon.gifstats.model.Sale
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.temporal.ChronoUnit

import java.util.*

class UtilsTest  {

    private val completeDateString = "2021-03-13 03:08:28 UTC"
    private val dateString = completeDateString.substring(0,10)
    private val timeString = completeDateString.substring(11,19)

    private fun getListSalesForMoney(list: List<Date>) : List<Sale>{
        val listReturn = mutableListOf<Sale>()
        for (date in list) {
            val sale = Sale(
                id = 1,
                objectName = "ObjectName",
                amount = 7.49,
                sourcePayment = "Source",
                dateDate =  date,
                dateString = dateString,
                hour =  timeString,
                email = "email",
                fullName = "FullName",
                donation = false,
                onSale = true,
                countryCode = "FR",
                ip = "IP",
                productPrice = 749.0,
                taxAdded = 1.5,
                tip = 0.0,
                marketPlaceFree = 0.82,
                sourceFee = 0.61,
                payout = "Payout",
                amountDelivered = 6.06,
                currency = "EUR",
                sourceId = "Source"
            )
            listReturn.add(sale)
        }
        return listReturn
    }

    @Test
    fun convertStringToDate_isCorrect() {
        //val dateString = "2021-03-13 03:08:28 UTC"
        val calendar = Calendar.getInstance()
        calendar.set(2021, 2,13, 3,8,28)
        val resultExpected = calendar.time.toInstant().truncatedTo(ChronoUnit.SECONDS)
        val result = Utils.convertStringToDate(completeDateString).toInstant().truncatedTo(ChronoUnit.SECONDS)

        assertEquals(resultExpected, result)
    }

    @Test
    fun convertStringToDateWithLocale_France_isCorrect() {
        //Time for France
        val resultExpected = arrayListOf("13/03/2021", "04:08:28")
        val result = Utils.convertStringToDateWithLocale(dateString, timeString, "dd/MM/yyyy")
        assertEquals(resultExpected, result)
    }

    @Test
    fun convertStringToDateWithLocale_US_isCorrect() {
        //TODO : Voir pour changer la locale du test
        //Time for France
        val resultExpected = arrayListOf("03/13/2021", "04:08:28")
        val result = Utils.convertStringToDateWithLocale(dateString, timeString, "MM/dd/yyyy")
        assertEquals(resultExpected, result)
    }

    @Test
    fun getDateStartToFilter_isCorrect() {
        val calendar = Calendar.getInstance()
        //Think to change with good date
        calendar.set(2021, 3, 13)
        val resultExpected = calendar.time.toInstant().truncatedTo(ChronoUnit.DAYS)
        val result = Utils.getDateStartToFilter(7).toInstant().truncatedTo(ChronoUnit.DAYS)
        assertEquals(resultExpected, result)
    }

    @Test
    fun convertDollarToEuros_isCorrect() {
        val resultExpected = 4.15
        val result = Utils.convertDollarToEuros(5.0)
        assertEquals(resultExpected, result,0.0)
    }

    @Test
    fun convertDollarToEuros_0_isCorrect() {
        val resultExpected = 0.0
        val result = Utils.convertDollarToEuros(0.0)
        assertEquals(resultExpected, result,0.0)
    }

    @Test
    fun convertDollarToEuros_01_isCorrect() {
        val resultExpected = 0.01
        val result = Utils.convertDollarToEuros(0.01)
        assertEquals(resultExpected, result,0.0)
    }

    @Test
    fun calculTotalNetSales_isCorrect() {
        val calendar = Calendar.getInstance()
        val listDates = arrayListOf(calendar.time, calendar.time, calendar.time)
        val listSales = getListSalesForMoney(listDates)
        val resultExpected = listSales[0].amountDelivered *3
        val result = Utils.calculTotalNetSales(listSales)
        assertEquals(resultExpected, result, 0.0)
    }

    @Test
    fun calculTotalNetSales_USD_isCorrect() {
        val calendar = Calendar.getInstance()
        val listDates = arrayListOf(calendar.time, calendar.time, calendar.time)
        val listSales = getListSalesForMoney(listDates)
        for (sale in listSales) {
            sale.currency = "USD"
        }
        val resultExpected = listSales[0].amountDelivered *3 * 0.83
        val result = Utils.calculTotalNetSales(listSales)
        assertEquals(resultExpected, result, 0.1)
    }

    @Test
    fun calculTotalBrutSales_isCorrect() {
        val calendar = Calendar.getInstance()
        val listDates = arrayListOf(calendar.time, calendar.time, calendar.time)
        val listSales = getListSalesForMoney(listDates)
        val resultExpected = listSales[0].amount *3
        val result = Utils.calculTotalBrutSales(listSales)
        assertEquals(resultExpected, result, 0.0)
    }

    @Test
    fun calculTotalBrutSales_USD_isCorrect() {
        val calendar = Calendar.getInstance()
        val listDates = arrayListOf(calendar.time, calendar.time, calendar.time)
        val listSales = getListSalesForMoney(listDates)
        for (sale in listSales) {
            sale.currency = "USD"
        }
        val resultExpected = listSales[0].amount *3 * 0.83
        val result = Utils.calculTotalBrutSales(listSales)
        assertEquals(resultExpected, result, 0.1)
    }

    @Test
    fun calculChargesSales_isCorrect() {
        val resultExpected = 16.08
        val result = Utils.calculChargesSales(2178.18, 1827.88)
        assertEquals(resultExpected, result, 0.0)
    }

    @Test
    fun xx_isCorrect() {
        val resultExpected = 0
        val result = 0
        assertEquals(resultExpected, result)
    }


}