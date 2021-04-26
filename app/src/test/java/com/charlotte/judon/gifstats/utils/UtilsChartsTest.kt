package com.charlotte.judon.gifstats.utils

import com.charlotte.judon.gifstats.model.CustomCurrency
import com.charlotte.judon.gifstats.model.Sale
import org.junit.Test
import org.junit.Assert
import java.text.SimpleDateFormat
import java.util.*

class UtilsChartsTest {

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

    private fun convertDateToString(date : Date): List<String> {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val stringDate = simpleDateFormat.format(date)
        return listOf(stringDate.substring(0, 10), stringDate.substring(11, 19))
    }

    private fun getListSales(list: List<Date>) : List<Sale>{
        val listReturn = mutableListOf<Sale>()
        for (date in list) {
            val convertDate = convertDateToString(date)
            val sale = Sale(
                id = 1,
                objectName = "ObjectName",
                amount = 7.49,
                sourcePayment = "Source",
                dateDate =  date,
                dateString = convertDate[0],
                hour =  convertDate[1],
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

    //Just to test the fun, but it is private
    /*@Test
    fun getDataForGraphByHour_isCorrect(){
        val calendar = Calendar.getInstance()
        val listDates = mutableListOf<Date>()
        calendar.set(2021,3,26,16,35)
        listDates.add(calendar.time)
        calendar.set(2021,3,25,16,25)
        listDates.add(calendar.time)
        calendar.set(2021,1,26,16,35)
        listDates.add(calendar.time)
        calendar.set(2020,6,3,18,35)
        listDates.add(calendar.time)
        calendar.set(2021,5,26,23,35)
        listDates.add(calendar.time)

        val listSales = getListSales(listDates)

        val resultExpectedNbTotal = 24 //Because 24H in 1 day
        val resultExpectedFor18H = 2f //Because in Time for France, just 1st and 2nd 16h = 18h
        val resultExpectedFor17H = 1f //Because in Time for France, just 3rd 16h = 17h
        val result = UtilsCharts.getDataForGraphByHour(listSales)
        Assert.assertEquals(resultExpectedNbTotal, result.size)
        Assert.assertEquals(resultExpectedFor18H, result[18].y)
        Assert.assertEquals(resultExpectedFor17H, result[17].y)
    }*/


    //Just to test the fun, but it is private
    /*@Test
    fun getDataForGraphByDayOfWeek_isCorrect(){
        val calendar = Calendar.getInstance()
        val listDates = mutableListOf<Date>()
        calendar.set(2021,3,26) //MONDAY
        listDates.add(calendar.time)
        calendar.set(2021,3,19) //MONDAY
        listDates.add(calendar.time)
        calendar.set(2021,1,26) //FRIDAY
        listDates.add(calendar.time)
        calendar.set(2021,0,3) //SUNDAY
        listDates.add(calendar.time)
        calendar.set(2021,2,12)  //FRIDAY
        listDates.add(calendar.time)

        val listSales = getListSales(listDates)

        val resultExpectedNbTotal = 7 //Because 7 days in week
        val resultExpectedForMonday = 2f
        val resultExpectedForFriday = 2f
        val resultExpectedForSunday = 1f
        val resultExpectedForOtherDays = 0f

        val result = UtilsCharts.getDataForGraphByDayOfWeek(listSales)
        Assert.assertEquals(resultExpectedNbTotal, result.size)
        Assert.assertEquals(resultExpectedForMonday, result[0].y)
        Assert.assertEquals(resultExpectedForFriday, result[4].y)
        Assert.assertEquals(resultExpectedForSunday, result[6].y)
        Assert.assertEquals(resultExpectedForOtherDays, result[1].y)
        Assert.assertEquals(resultExpectedForOtherDays, result[2].y)
        Assert.assertEquals(resultExpectedForOtherDays, result[3].y)
        Assert.assertEquals(resultExpectedForOtherDays, result[5].y)
    }*/

    //Just to test the fun, but it is private
    /*@Test
    fun getListForOneDay_isCorrect(){
        val calendar = Calendar.getInstance()
        val listDates = mutableListOf<Date>()
        calendar.set(2021,3,26) //MONDAY
        listDates.add(calendar.time)
        calendar.set(2021,3,26) //MONDAY
        listDates.add(calendar.time)
        calendar.set(2021,1,26) //FRIDAY
        listDates.add(calendar.time)
        calendar.set(2021,0,3) //SUNDAY
        listDates.add(calendar.time)
        calendar.set(2021,2,12)  //FRIDAY
        listDates.add(calendar.time)

        val listSales = getListSales(listDates)

        val resultExpectedNbTotal = 2
        val result = UtilsCharts.getListForOneDay(listSales, "Mon")
        Assert.assertEquals(resultExpectedNbTotal, result.size)
    }*/

    //Just to test the fun, but it is private
    /*@Test
    fun getDataForGraphByDayWithHours_isCorrect(){
        val calendar = Calendar.getInstance()
        val listDates = mutableListOf<Date>()
        calendar.set(2021,3,26,16,35) //MONDAY - 18h
        listDates.add(calendar.time)
        calendar.set(2021,3,19,16,25) //MONDAY - 18h
        listDates.add(calendar.time)
        calendar.set(2021,1,8,16,35) //MONDAY - 17h
        listDates.add(calendar.time)
        calendar.set(2021,0,18,17,35) //SUNDAY - 18h
        listDates.add(calendar.time)
        calendar.set(2021,2,29,21,35) //MONDAY - 23h
        listDates.add(calendar.time)

        val listSales = getListSales(listDates)

        val resultExpectedNbTotal = 24 //Because 24h in a day
        val resultExpectedFor18h = 3f
        val resultExpectedFor17h = 1f
        val resultExpectedFor23h = 1f
        val resultExpectedForOtherHours = 0f

        val result = UtilsCharts.getDataForGraphByDaysWithHours(listSales)
        Assert.assertEquals(resultExpectedNbTotal, result.size)
        Assert.assertEquals(resultExpectedFor18h, result[18].y)
        Assert.assertEquals(resultExpectedFor17h, result[17].y)
        Assert.assertEquals(resultExpectedFor23h, result[23].y)
        Assert.assertEquals(resultExpectedForOtherHours, result[0].y)
        Assert.assertEquals(resultExpectedForOtherHours, result[19].y)
    }*/

    //Just to test the fun, but it is private
    @Test
    fun getDataForGraphByDay_isCorrect(){

        //TODO : A revoir !!!! 
        val calendar = Calendar.getInstance()
        val listDates = mutableListOf<Date>()
        calendar.set(2021,3,18)
        listDates.add(calendar.time)
        calendar.set(2021,3,20)
        listDates.add(calendar.time)
        calendar.set(2021,3,20)
        listDates.add(calendar.time)
        calendar.set(2021,3,20)
        listDates.add(calendar.time)
        calendar.set(2021,3,25)
        listDates.add(calendar.time)


        val listSales = getListSales(listDates)

        val resultExpectedNbTotal = 8 //8 days between 18th and 25th of April
        val resultExpectedFor20 = 3f
        val resultExpectedFor18 = 1f
        val resultExpectedFor26 = 1f
        val resultExpectedForOtherDays = 0f

        val result = UtilsCharts.getDataForGraphByDay(listSales)
        Assert.assertEquals(resultExpectedNbTotal, result.size)
        Assert.assertEquals(resultExpectedFor20, result[2].y)
        Assert.assertEquals(resultExpectedFor18, result[0].y)
        Assert.assertEquals(resultExpectedFor26, result[7].y)
        Assert.assertEquals(resultExpectedForOtherDays, result[3].y)
        Assert.assertEquals(resultExpectedForOtherDays, result[5].y)
    }

}