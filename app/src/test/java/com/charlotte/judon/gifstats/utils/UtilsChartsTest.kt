package com.charlotte.judon.gifstats.utils

import com.charlotte.judon.gifstats.model.CustomCurrency
import com.charlotte.judon.gifstats.model.Sale
import org.junit.Test
import org.junit.Assert
import java.text.SimpleDateFormat
import java.util.*

class UtilsChartsTest {

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
                currency = "USD",
                sourceId = "Source"
            )
            listReturn.add(sale)
        }
        return listReturn
    }

    @Test
     fun anyChartPackagePrice_isCorrect(){

        val calendar = Calendar.getInstance()
        calendar.set(2021,3,18)
        val listDates = listOf(calendar.time, calendar.time, calendar.time, calendar.time,calendar.time)

        val listSales = getListSales(listDates)
        listSales[0].objectName="Package1"
        listSales[1].objectName="Package1"
        listSales[2].objectName="Package1"
        listSales[3].objectName="Package2"
        listSales[3].amountDelivered=7.0
        listSales[4].objectName="Package4"

        val resultExpectedNbTotal = 3
        val resultExpectedForPackage1 = (listSales[0].amountDelivered *3).toString()
        val resultExpectedForPackage2 = (listSales[3].amountDelivered).toString()
        val resultExpectedForPackage4 = (listSales[4].amountDelivered).toString()

        val result = UtilsCharts.anyChartPackagePrice(listSales, listCurrencies[0], listCurrencies)
        Assert.assertEquals(resultExpectedNbTotal, result.size)
        Assert.assertEquals(resultExpectedForPackage1, result[0].getValue("value"))
        Assert.assertEquals(resultExpectedForPackage2, result[1].getValue("value"))
        Assert.assertEquals(resultExpectedForPackage4, result[2].getValue("value"))
        Assert.assertEquals("Package1", result[0].getValue("x"))
     }

    @Test
    fun anyChartPackageNB_isCorrect(){

        val calendar = Calendar.getInstance()
        calendar.set(2021,3,18)
        val listDates = listOf(calendar.time, calendar.time, calendar.time, calendar.time,calendar.time)

        val listSales = getListSales(listDates)
        listSales[0].objectName="Package1"
        listSales[1].objectName="Package1"
        listSales[2].objectName="Package1"
        listSales[3].objectName="Package2"
        listSales[3].amountDelivered=7.0
        listSales[4].objectName="Package4"

        val resultExpectedNbTotal = 3
        val resultExpectedForPackage1 = 3.toString()
        val resultExpectedForPackage2 = 1.toString()
        val resultExpectedForPackage4 = 1.toString()

        val result = UtilsCharts.anyChartPackageNB(listSales)
        Assert.assertEquals(resultExpectedNbTotal, result.size)
        Assert.assertEquals(resultExpectedForPackage1, result[0].getValue("value"))
        Assert.assertEquals(resultExpectedForPackage2, result[1].getValue("value"))
        Assert.assertEquals(resultExpectedForPackage4, result[2].getValue("value"))
        Assert.assertEquals("Package1", result[0].getValue("x"))
    }

    @Test
    fun graphAnyChartMapChronopleth_isCorrect(){

        val calendar = Calendar.getInstance()
        calendar.set(2021,3,18)
        val listDates = listOf(calendar.time, calendar.time, calendar.time, calendar.time,calendar.time)

        val listSales = getListSales(listDates)
        listSales[0].countryCode="FR"
        listSales[1].countryCode="FR"
        listSales[2].countryCode="FR"
        listSales[3].countryCode="IT"
        listSales[4].countryCode="US"

        val resultExpectedForPackage1 = 3.toString()
        val resultExpectedForPackage2 = 1.toString()
        val resultExpectedForPackage4 = 1.toString()

        val result = UtilsCharts.graphAnyChartMapChronopleth(listSales)
        Assert.assertEquals(resultExpectedForPackage1, result.listEntry[0].getValue("value"))
        Assert.assertEquals(resultExpectedForPackage2, result.listEntry[1].getValue("value"))
        Assert.assertEquals(resultExpectedForPackage4, result.listEntry[2].getValue("value"))
        Assert.assertEquals("FR", result.listCustomCountry[0].country)
        Assert.assertEquals(3, result.listCustomCountry[0].nb)
        Assert.assertEquals(3, result.max)
        Assert.assertEquals(1, result.max2)
    }

    @Test
    fun getRanges_200_50_isCorrect(){

        val resultExpected = getArrayRanges(12,24,36,50)
        val result = UtilsCharts.getRanges(200, 50)

        Assert.assertEquals(resultExpected[0], result[0])
        Assert.assertEquals(resultExpected[1], result[1])
        Assert.assertEquals(resultExpected[2], result[2])
        Assert.assertEquals(resultExpected[3], result[3])
        Assert.assertEquals(resultExpected[4], result[4])

    }

    @Test
    fun getRanges_200_180_isCorrect(){

        val resultExpected = getArrayRanges(40,80,120,160)
        val result = UtilsCharts.getRanges(200, 180)

        Assert.assertEquals(resultExpected[0], result[0])
        Assert.assertEquals(resultExpected[1], result[1])
        Assert.assertEquals(resultExpected[2], result[2])
        Assert.assertEquals(resultExpected[3], result[3])
        Assert.assertEquals(resultExpected[4], result[4])

    }

    @Test
    fun getRanges_30_30_isCorrect(){

        val resultExpected = getArrayRanges(6,12,18,24)
        val result = UtilsCharts.getRanges(30, 30)

        Assert.assertEquals(resultExpected[0], result[0])
        Assert.assertEquals(resultExpected[1], result[1])
        Assert.assertEquals(resultExpected[2], result[2])
        Assert.assertEquals(resultExpected[3], result[3])
        Assert.assertEquals(resultExpected[4], result[4])

    }

    @Test
    fun getRanges_9_0_isCorrect(){

        val resultExpected = arrayOf("{less: 1}", "{greater: 1}")
        val result = UtilsCharts.getRanges(9, 0)
        Assert.assertEquals(resultExpected[0], result[0])
        Assert.assertEquals(resultExpected[1], result[1])
    }

    @Test
    fun getRanges_30_0_isCorrect(){

        val resultExpected = arrayOf("{less: 1}", "{greater: 1}")
        val result = UtilsCharts.getRanges(30, 0)
        Assert.assertEquals(resultExpected[0], result[0])
        Assert.assertEquals(resultExpected[1], result[1])
    }

    private fun getArrayRanges(nb1 : Int, nb2 : Int, nb3 : Int, nb4 : Int) : Array<String>{
        return arrayOf(
            "{less: 1}",
            "{from: 1, to: $nb1}",
            "{from: $nb1, to: $nb2}",
            "{from: $nb2, to: $nb3}",
            "{from: $nb3, to: $nb4}"
        )
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

    //Just to test the fun
   /* @Test
    fun getDataForGraphByDay_isCorrect(){

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

        val resultExpectedNbTotal = 10 //10 days between 18th and 27th of April
        val resultExpectedFor20 = 3f
        val resultExpectedFor18 = 1f
        val resultExpectedFor26 = 1f
        val resultExpectedForOtherDays = 0f

        val result = UtilsCharts.getDataForGraphByDay(listSales, "dd/MM")
        Assert.assertEquals(resultExpectedNbTotal, result.size)
        Assert.assertEquals(resultExpectedFor20, result[2].y)
        Assert.assertEquals(resultExpectedFor18, result[0].y)
        Assert.assertEquals(resultExpectedFor26, result[7].y)
        Assert.assertEquals(resultExpectedForOtherDays, result[3].y)
        Assert.assertEquals(resultExpectedForOtherDays, result[5].y)
    }*/

}