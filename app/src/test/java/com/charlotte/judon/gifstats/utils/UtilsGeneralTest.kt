package com.charlotte.judon.gifstats.utils

import com.charlotte.judon.gifstats.model.CustomCurrency
import com.charlotte.judon.gifstats.model.Sale
import org.junit.Assert.*
import org.junit.Test
import java.time.temporal.ChronoUnit

import java.util.*

class UtilsGeneralTest  {

    private val completeDateString = "2021-03-13 03:08:28 UTC"
    private val dateString = completeDateString.substring(0,10)
    private val timeString = completeDateString.substring(11,19)
    private val listCurrencies = listOf(
        CustomCurrency("USD", 1.0, 1.0, "$"),
        CustomCurrency("CAD", 1.26, 0.80, "$"),
        CustomCurrency("GBP", 0.72, 1.39, "£"),
        CustomCurrency("EUR", 0.83, 1.20, "€"),
        CustomCurrency("JPY", 108.10, 0.01, "¥"),
        CustomCurrency("AUD", 1.29, 0.77, "$"),
    )

    private fun getListSales(list: List<Date>) : List<Sale>{
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
        val calendar = Calendar.getInstance()
        calendar.set(2021, 2,13, 3,8,28)
        val resultExpected = calendar.time.toInstant().truncatedTo(ChronoUnit.SECONDS)
        val result = UtilsGeneral.convertStringToDate(completeDateString).toInstant().truncatedTo(ChronoUnit.SECONDS)

        assertEquals(resultExpected, result)
    }

    @Test
    fun convertStringToDateWithLocale_France_isCorrect() {
        //Time for France
        val resultExpected = arrayListOf("13/03", "04:08")
        val result = UtilsGeneral.convertStringToDateWithLocale(dateString, timeString, "dd/MM")
        assertEquals(resultExpected, result)
    }

    @Test
    fun convertStringToDateWithLocale_US_isCorrect() {
        //TODO : Voir pour changer la locale du test
        //Time for France
        val resultExpected = arrayListOf("03/13/2021", "04:08")
        val result = UtilsGeneral.convertStringToDateWithLocale(dateString, timeString, "MM/dd/yyyy")
        assertEquals(resultExpected, result)
    }

    @Test
    fun getDateStartToFilter_isCorrect() {
        val calendar = Calendar.getInstance()
        //Think to change with good date
        calendar.set(2021, 3, 19)
        val resultExpected = calendar.time.toInstant().truncatedTo(ChronoUnit.DAYS)
        val result = UtilsGeneral.getDateStartToFilter(7).toInstant().truncatedTo(ChronoUnit.DAYS)
        assertEquals(resultExpected, result)
    }

    @Test
    fun calculTotalNetSales_isCorrect() {
        val calendar = Calendar.getInstance()
        val listDates = arrayListOf(calendar.time, calendar.time, calendar.time)
        val listSales = getListSales(listDates)
        val resultExpected = listSales[0].amountDelivered *3 * listCurrencies[3].inverseRate
        val result = UtilsGeneral.calculTotalNetSales(listSales, listCurrencies[0], listCurrencies)
        assertEquals(resultExpected, result, 0.1)
    }

    @Test
    fun calculTotalNetSales_USD_isCorrect() {
        val calendar = Calendar.getInstance()
        val listDates = arrayListOf(calendar.time, calendar.time, calendar.time)
        val listSales = getListSales(listDates)
        for (sale in listSales) {
            sale.currency = "USD"
        }
        val resultExpected = listSales[0].amountDelivered *3
        val result = UtilsGeneral.calculTotalNetSales(listSales, listCurrencies[0], listCurrencies)
        assertEquals(resultExpected, result, 0.1)
    }

    @Test
    fun calculTotalBrutSales_isCorrect() {
        val calendar = Calendar.getInstance()
        val listDates = arrayListOf(calendar.time, calendar.time, calendar.time)
        val listSales = getListSales(listDates)
        val resultExpected = listSales[0].amount *3 * listCurrencies[3].inverseRate
        val result = UtilsGeneral.calculTotalBrutSales(listSales, listCurrencies[0], listCurrencies)
        assertEquals(resultExpected, result, 0.1)
    }

    @Test
    fun calculTotalBrutSales_USD_isCorrect() {
        val calendar = Calendar.getInstance()
        val listDates = arrayListOf(calendar.time, calendar.time, calendar.time)
        val listSales = getListSales(listDates)
        for (sale in listSales) {
            sale.currency = "USD"
        }
        val resultExpected = listSales[0].amount *3
        val result = UtilsGeneral.calculTotalBrutSales(listSales, listCurrencies[0], listCurrencies)
        assertEquals(resultExpected, result, 0.1)
    }


    @Test
    fun calculChargesSales_isCorrect() {
        val resultExpected = 16.08
        val result = UtilsGeneral.calculChargesSales(2178.18, 1827.88)
        assertEquals(resultExpected, result, 0.0)
    }

    @Test
    fun calculateDiffBetweenTwoDate_SAME_isCorrect() {
        val resultExpected = UtilsGeneral.CompareDates.SAME
        val date = UtilsGeneral.convertStringToDate("2020-11-19 00:13:48 UTC")
        val result = UtilsGeneral.calculateDiffBetweenTwoDates(date, date)
        assertEquals(resultExpected, result)
    }

    @Test
    fun calculateDiffBetweenTwoDate_PLUS_ONE_isCorrect() {
        val resultExpected = UtilsGeneral.CompareDates.PLUS_ONE
        val date1 = UtilsGeneral.convertStringToDate("2020-11-19 00:13:48 UTC")
        val date2 = UtilsGeneral.convertStringToDate("2020-11-20 00:13:48 UTC")
        val result = UtilsGeneral.calculateDiffBetweenTwoDates(date1, date2)
        assertEquals(resultExpected, result)
    }

    @Test
    fun calculateDiffBetweenTwoDate_PLUS_OTHER_YEAR_isCorrect() {
        val resultExpected = UtilsGeneral.CompareDates.PLUS_OTHER
        val date1 = UtilsGeneral.convertStringToDate("2019-11-19 00:13:48 UTC")
        val date2 = UtilsGeneral.convertStringToDate("2020-11-19 00:13:48 UTC")
        val result = UtilsGeneral.calculateDiffBetweenTwoDates(date1, date2)
        assertEquals(resultExpected, result)
    }

    @Test
    fun calculateDiffBetweenTwoDate_PLUS_OTHER_DAYS_isCorrect() {
        val resultExpected = UtilsGeneral.CompareDates.PLUS_OTHER
        val date1 = UtilsGeneral.convertStringToDate("2020-11-19 00:13:48 UTC")
        val date2 = UtilsGeneral.convertStringToDate("2020-11-23 00:13:48 UTC")
        val result = UtilsGeneral.calculateDiffBetweenTwoDates(date1, date2)
        assertEquals(resultExpected, result)
    }

    @Test
    fun calculateDiffBetweenTwoDate_PLUS_OTHER_MONTH_isCorrect() {
        val resultExpected = UtilsGeneral.CompareDates.PLUS_OTHER
        val date1 = UtilsGeneral.convertStringToDate("2020-11-19 00:13:48 UTC")
        val date2 = UtilsGeneral.convertStringToDate("2020-12-19 00:13:48 UTC")
        val result = UtilsGeneral.calculateDiffBetweenTwoDates(date1, date2)
        assertEquals(resultExpected, result)
    }

    @Test
    fun calculateNumberOfDaysOfDifference_0L_isCorrect() {
        val resultExpected = 0L
        val date = UtilsGeneral.convertStringToDate("2020-11-19 00:13:48 UTC")
        val result = UtilsGeneral.calculateNumberOfDaysOfDifference(date, date)
        assertEquals(resultExpected, result)
    }

    @Test
    fun calculateNumberOfDaysOfDifference_1L_isCorrect() {
        val resultExpected = 1L
        val date1 = UtilsGeneral.convertStringToDate("2020-11-19 00:13:48 UTC")
        val date2 = UtilsGeneral.convertStringToDate("2020-11-20 00:13:48 UTC")
        val result = UtilsGeneral.calculateNumberOfDaysOfDifference(date1, date2)
        assertEquals(resultExpected, result)
    }

    @Test
    fun calculateNumberOfDaysOfDifference_2L_isCorrect() {
        val resultExpected = 2L
        val date1 = UtilsGeneral.convertStringToDate("2020-11-19 11:13:48 UTC")
        val date2 = UtilsGeneral.convertStringToDate("2020-11-21 00:13:48 UTC")
        val result = UtilsGeneral.calculateNumberOfDaysOfDifference(date1, date2)
        assertEquals(resultExpected, result)
    }

    @Test
    fun calculateNumberOfDaysOfDifference_3L_isCorrect() {
        val resultExpected = 3L
        val date1 = UtilsGeneral.convertStringToDate("2020-11-19 23:59:48 UTC")
        val date2 = UtilsGeneral.convertStringToDate("2020-11-22 00:13:48 UTC")
        val result = UtilsGeneral.calculateNumberOfDaysOfDifference(date1, date2)
        assertEquals(resultExpected, result)
    }


    @Test
    fun filterList_7Days_isCorrect() {
        val calendar = Calendar.getInstance()
        val date1 = calendar.time
        calendar.set(2021, 3, 20)
        val date2 = calendar.time
        calendar.set(2021, 3, 15)
        val date3 = calendar.time
        val listSales = getListSales(listOf(date1, date2, date3))
        val dateStart = UtilsGeneral.getDateStartToFilter(7)
        val resultExpected = 2
        val result = UtilsGeneral.filterList(listSales, dateStart).size
        assertEquals(resultExpected, result)
    }

    @Test
    fun filterList_30Days_isCorrect() {
        val calendar = Calendar.getInstance()
        val date1 = calendar.time
        calendar.set(2021, 3, 20)
        val date2 = calendar.time
        calendar.set(2021, 3, 15)
        val date3 = calendar.time
        val listSales = getListSales(listOf(date1, date2, date3))
        val dateStart = UtilsGeneral.getDateStartToFilter(30)
        val resultExpected = 3
        val result = UtilsGeneral.filterList(listSales, dateStart).size
        assertEquals(resultExpected, result)
    }

    @Test
    fun castSaleListInSaleMonthList_isCorrect() {
        val listSales = getListSales(getListDates())
        val resultExpectedSize = 6
        val resultExpectedNb = 6
        val resultExpectedNet = listSales[0].amountDelivered*6
        val result = UtilsGeneral.castSaleListInSaleMonthList(listSales, listCurrencies[3], listCurrencies)
        assertEquals(resultExpectedSize, result.size)
        assertEquals("Jul", result[0].month)
        assertEquals("2021", result[0].year)
        assertFalse(result[0].isInProgress)
        assertEquals(resultExpectedNb, result[0].list.size)
        assertEquals(resultExpectedNet, result[0].totalPrice, 0.01)
    }

    private fun getListDates() : List<Date>{
        val calendar = Calendar.getInstance()
        val list = mutableListOf<Date>()

        for (month in 1..6 ) {
            for (day in 1 ..28 step 5) {
                calendar.set(2021, month, day)
                list.add(calendar.time)
            }
        }

        return list
    }

}