package com.charlotte.judon.gifstats.utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.ChronopletReturn
import com.charlotte.judon.gifstats.model.DateDetail
import com.charlotte.judon.gifstats.model.MonthSale
import com.charlotte.judon.gifstats.model.Sale
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_graphs.view.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

class Utils {

    companion object {

        fun convertStringToDate(dateStringFromCSV: String): Date {

            val dateString = dateStringFromCSV.substring(0, 10)
            val hourString = dateStringFromCSV.substring(11, 19)

            val completeDateString = "$dateString $hourString+0000"
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS]Z")
            val resultOfParsing = OffsetDateTime.parse(completeDateString, dateFormatter)
            val timeZone = ZoneId.of("Europe/Paris")
            val franceTime = resultOfParsing.atZoneSameInstant(timeZone)
            return Date.from(franceTime.toInstant())
        }

        fun getDateStartToFilter(daysAgo: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

            return calendar.time
        }

        fun filterList(list: List<Sale>, dateStart: Date): List<Sale> {
            val listToReturn = arrayListOf<Sale>()
            for (sale in list) {
                val dateTemp = sale.dateDate
                if (dateTemp.toInstant().truncatedTo(ChronoUnit.DAYS) >= dateStart.toInstant()
                        .truncatedTo(ChronoUnit.DAYS)
                ) {
                    listToReturn.add(sale)
                }
            }
            return listToReturn
        }

        fun castSaleListInSaleMonthList(salesList: List<Sale>): List<MonthSale> {
            val listToReturn = arrayListOf<MonthSale>()
            var listForOneMonth = arrayListOf<Sale>()

            val dateOfToday = Date().toString().substring(4, 7)
            var refMonth = salesList[0].dateDate.toString().substring(4, 7)

            for (sale in salesList) {
                val monthTemp = sale.dateDate.toString().substring(4, 7)

                if (monthTemp == refMonth) {
                    listForOneMonth.add(sale)
                } else {
                    val total = calculTotalNetSales(listForOneMonth)
                    val bool = refMonth != dateOfToday
                    listToReturn.add(
                        MonthSale(
                            refMonth,
                            listForOneMonth.size,
                            total,
                            bool,
                            listForOneMonth
                        )
                    )
                    refMonth = monthTemp
                    listForOneMonth = arrayListOf()
                }

                if (salesList.indexOf(sale) == salesList.size - 1) {
                    val total = calculTotalNetSales(listForOneMonth)
                    val bool = monthTemp != dateOfToday
                    listToReturn.add(
                        MonthSale(
                            monthTemp,
                            listForOneMonth.size,
                            total,
                            bool,
                            listForOneMonth
                        )
                    )
                }
            }
            return listToReturn.reversed()
        }

        fun convertDollarToEuros(price: Double): Double {
            val decimalFormat = DecimalFormat("####0.00")
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'
            decimalFormat.decimalFormatSymbols = separator
            return decimalFormat.format(price * 0.82).toDouble()
        }

        fun calculTotalNetSales(list: List<Sale>): Double {
            var total = 0.0
            val decimalFormat = DecimalFormat("####0.00")
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'
            decimalFormat.decimalFormatSymbols = separator

            for (sale in list) {
                total += if (sale.currency == "USD") {
                    convertDollarToEuros(sale.amountDelivered)
                } else {
                    sale.amountDelivered
                }

            }
            return decimalFormat.format(total).toDouble()
        }

        fun calculTotalBrutSales(list: List<Sale>): Double {
            var total = 0.0
            val decimalFormat = DecimalFormat("####0.00")
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'
            decimalFormat.decimalFormatSymbols = separator

            for (sale in list) {
                total += if (sale.currency == "USD") {
                    convertDollarToEuros(sale.amount)
                } else {
                    sale.amountDelivered
                }

            }
            return decimalFormat.format(total).toDouble()
        }

        fun calculChargesSales(brut : Double, net : Double): Double {

            val decimalFormat = DecimalFormat("####0.00")
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'
            decimalFormat.decimalFormatSymbols = separator

            val total = ((brut - net)/brut) * 100

            return decimalFormat.format(total).toDouble()
        }

        private fun sortByCountry(list: List<Sale>): List<Sale> {
            return list.sortedWith(compareBy { it.countryCode })
        }

        private fun sortSalesByDate(list: List<Sale>): List<Sale> {
            return list.sortedWith(compareBy { it.dateDate })
        }

        private fun sortSalesByHour(list: List<Sale>): List<Sale> {
            return list.sortedWith(compareBy { it.hour })
        }

        private fun sortSalesByPackage(list: List<Sale>): List<Sale> {
            return list.sortedWith(compareBy { it.objectName })
        }

        fun graphMPByDay(salesList: List<Sale>, view: View) {

            val listSorted = Utils.sortSalesByDate(salesList)
            val listEntry = arrayListOf<BarEntry>()
            val listString = arrayListOf<String>()
            val formatterDate = SimpleDateFormat("dd/MM", Locale.getDefault())

            var nbPerDay = 1f
            var day = listSorted[0].dateDate

            for (sale in listSorted) {
                val dateTemp = sale.dateDate
                val calendarToCompare = Calendar.getInstance()
                calendarToCompare.time = day
                calendarToCompare.add(Calendar.DAY_OF_YEAR, 1)
                val dayPlus1 = calendarToCompare.time
                var isLastAdding = false

                if (dateTemp.toInstant().truncatedTo(ChronoUnit.DAYS) == dayPlus1.toInstant().truncatedTo(ChronoUnit.DAYS)) {
                    val dateString = formatterDate.format(dateTemp)
                    listEntry.add(BarEntry(listString.size.toFloat(), nbPerDay))
                    listString.add(dateString)
                    day = dateTemp
                    nbPerDay = 1f
                    isLastAdding = true
                } else if (dateTemp.toInstant().truncatedTo(ChronoUnit.DAYS) > dayPlus1.toInstant().truncatedTo(ChronoUnit.DAYS)) {

                    val difLong = dateTemp.time - day.time
                    val difInt = TimeUnit.DAYS.convert(difLong, TimeUnit.MILLISECONDS)

                    for (i in 0 until difInt) {
                        val calendarTemp = Calendar.getInstance()
                        calendarTemp.time = day
                        calendarTemp.add(Calendar.DAY_OF_YEAR, i.toInt()+1)
                        val dateString = formatterDate.format(calendarTemp.time)
                        listEntry.add(BarEntry(listString.size.toFloat(), 0f))
                        listString.add(dateString)
                    }
                    val dateString = formatterDate.format(dateTemp)
                    listEntry.add(BarEntry(listString.size.toFloat(), nbPerDay))
                    listString.add(dateString)
                    day = dateTemp
                    nbPerDay = 1f
                    isLastAdding = true
                }
                else {
                    nbPerDay++
                }

                if (listSorted.indexOf(sale) == listSorted.size - 1 && !isLastAdding) {
                    val dateString = formatterDate.format(day)

                    listEntry.add(BarEntry(listString.size.toFloat(), nbPerDay))
                    listString.add(dateString)
                }
            }

            val calendarNow = Calendar.getInstance()
            val timeNow = calendarNow.time

            if(day.time != timeNow.time) {
                val difLong = timeNow.time - day.time
                val difInt = TimeUnit.DAYS.convert(difLong, TimeUnit.MILLISECONDS)

                for (i in 0 until difInt) {
                    val calendarTemp = Calendar.getInstance()
                    calendarTemp.time = day
                    calendarTemp.add(Calendar.DAY_OF_YEAR, i.toInt() +1)
                    val dateString = formatterDate.format(calendarTemp.time)
                    listEntry.add(BarEntry(listString.size.toFloat(), 0f))
                    listString.add(dateString)
                }
            }

            val barDataSet = BarDataSet(listEntry, " ")
            barDataSet.color = Color.parseColor("#F80039")
            barDataSet.highLightColor = Color.parseColor("#4B2E5A")
            barDataSet.setDrawValues(false)

            val data = BarData(barDataSet)
            val formatterX = IndexAxisValueFormatter(listString)
            view.MpBarView.data = data
            view.MpBarView.description.isEnabled = false
            view.MpBarView.xAxis.position = XAxis.XAxisPosition.BOTTOM
            view.MpBarView.xAxis.valueFormatter = formatterX
            view.MpBarView.axisLeft.axisMinimum = 0f
            view.MpBarView.axisRight.axisMinimum = 0f

        }

        fun graphMPBarByHour(salesList: List<Sale>, view: View) {

            val listSorted = sortSalesByHour(salesList)
            val listEntry = arrayListOf<BarEntry>()
            val listString = arrayListOf<String>()

            var nbPerHour = 0
            var hour = 0

            for (sale in listSorted) {

                val hourString = sale.hour.substring(0, 2)
                val hourSale = hourString.toInt()
                if (hourSale > hour) {
                    listEntry.add(BarEntry(hour.toFloat(), nbPerHour.toFloat()))
                    nbPerHour = 1
                    hour = hourSale
                } else {
                    nbPerHour++
                }
                if (listSorted.indexOf(sale) == listSorted.size - 1) {
                    listEntry.add(BarEntry(hour.toFloat(), nbPerHour.toFloat()))
                }
            }

            /*for (sale in listSorted) {

                val hourString = sale.hour.substring(0, 2)
                val hourSale = hourString.toInt()
                if (hourSale == hour+1) {
                    listEntry.add(BarEntry(hour.toFloat(), nbPerHour.toFloat()))
                    //val tempString = hour.toString().split(".")
                    listString.add("${hourString}H")
                    nbPerHour = 1
                    hour = hourSale
                } else if (hourSale > hour +1){
                    val difHour = hourSale - hour
                    for (i in 1 until difHour) {
                        //val tempString = hour.toString().split(".")[0].toInt()
                        //val hourTemp = tempString + i
                        val hourTemp = hourSale + i
                        listEntry.add(BarEntry((hourTemp).toFloat(), 0f))
                        listString.add("${hourTemp}H")
                    }
                    listEntry.add(BarEntry(hour.toFloat(), nbPerHour.toFloat()))
                    //val tempString = hour.toString().split(".")
                    listString.add("${hourString[0]}H")
                    nbPerHour = 1
                    hour = hourSale
                }
                else {
                    nbPerHour++
                }

                if (listSorted.indexOf(sale) == listSorted.size - 1) {
                    listEntry.add(BarEntry(hour.toFloat(), nbPerHour.toFloat()))
                    listString.add("${hour}H")
                }
            }

            if (listEntry.size < 24 ) {
                for (i in 23 downTo listEntry.size) {
                    listEntry.add(BarEntry((i).toFloat(), 0f))
                    listString.add("${i}H")
                }
            }*/
            val barDataSet = BarDataSet(listEntry, " ")
            barDataSet.color = Color.parseColor("#F80039")
            barDataSet.highLightColor = Color.parseColor("#4B2E5A")
            barDataSet.setDrawValues(false)

            val data = BarData(barDataSet)
            //val formatterX = IndexAxisValueFormatter(listString)
            view.MpBarView.data = data
            view.MpBarView.description.isEnabled = false
            view.MpBarView.xAxis.position = XAxis.XAxisPosition.BOTTOM
            //view.MpBarView.xAxis.valueFormatter = formatterX
            view.MpBarView.xAxis.valueFormatter = null
            view.MpBarView.axisLeft.axisMinimum = 0f
            view.MpBarView.axisRight.axisMinimum = 0f

        }

        private fun getListForOneDay(list: List<Sale>, day : String) : List<Sale> {
            val listToReturn = arrayListOf<Sale>()
            for (sale in list) {
                if (sale.dateDate.toString().substring(0,3) == day) {
                    listToReturn.add(sale)
                }

            }
            return listToReturn
        }


        fun graphMPBarByDayWithHours(list: List<Sale>, day : String, context : Context)  : DateDetail {
            val listEntry = arrayListOf<BarEntry>()
            val listSorted = sortSalesByHour(getListForOneDay(list, day))

            var hour = 0
            var nbPerHour = 0

            for (sale in listSorted) {

                val hourString = sale.hour.substring(0, 2)
                val hourSale = hourString.toInt()
                if (hourSale > hour) {
                    listEntry.add(BarEntry(hour.toFloat(), nbPerHour.toFloat()))
                    nbPerHour = 1
                    hour = hourSale
                } else {
                    nbPerHour++
                }
                if (listSorted.indexOf(sale) == listSorted.size - 1) {
                    listEntry.add(BarEntry(hour.toFloat(), nbPerHour.toFloat()))
                }
            }

            var name = ""
            val listDay = arrayListOf<String>("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val listDayFromResources = context.resources.getStringArray(R.array.list_days)
            when (day) {
                listDay[0] -> name = listDayFromResources[0]
                listDay[1] -> name = listDayFromResources[1]
                listDay[2] -> name = listDayFromResources[2]
                listDay[3] -> name = listDayFromResources[3]
                listDay[4] -> name = listDayFromResources[4]
                listDay[5] -> name = listDayFromResources[5]
                listDay[6] -> name = listDayFromResources[6]
            }
            return DateDetail(name, listEntry, null, listSorted.size)
        }

        fun graphMPBarByDayOfWeek(list: List<Sale>, view: View) {

            var nbMonday = 0
            var nbTuesday = 0
            var nbWednesday = 0
            var nbThursday = 0
            var nbFriday = 0
            var nbSaturday = 0
            var nbSunday = 0

            val listDay = arrayListOf<String>("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

            for (sale in list) {

                when (sale.dateDate.toString().substring(0, 3)) {
                    listDay[0] -> nbMonday++
                    listDay[1] -> nbTuesday++
                    listDay[2] -> nbWednesday++
                    listDay[3] -> nbThursday++
                    listDay[4] -> nbFriday++
                    listDay[5] -> nbSaturday++
                    listDay[6] -> nbSunday++
                }
            }
            val listBarEntry = arrayListOf<BarEntry>(
                BarEntry(0f, nbMonday.toFloat()),
                BarEntry(1f, nbTuesday.toFloat()),
                BarEntry(2f, nbWednesday.toFloat()),
                BarEntry(3f, nbThursday.toFloat()),
                BarEntry(4f, nbFriday.toFloat()),
                BarEntry(5f, nbSaturday.toFloat()),
                BarEntry(6f, nbSunday.toFloat())
            )

            val barDataSet = BarDataSet(listBarEntry, " ")
            barDataSet.color = Color.parseColor("#F80039")
            barDataSet.highLightColor = Color.parseColor("#4B2E5A")
            val formatterX = IndexAxisValueFormatter(listDay)

            barDataSet.setDrawValues(false)
            val data = BarData(barDataSet)
            view.MpBarView.data = data
            view.MpBarView.description.isEnabled = false
            view.MpBarView.xAxis.position = XAxis.XAxisPosition.BOTTOM
            view.MpBarView.xAxis.valueFormatter = formatterX
            view.MpBarView.axisLeft.axisMinimum = 0f
            view.MpBarView.axisRight.axisMinimum = 0f

        }


        fun anyChartPackagePrice(list: List<Sale>): MutableList<DataEntry> {
            val listSorted = sortSalesByPackage(list)
            val listPrice = mutableListOf<DataEntry>()
            var namePackage = listSorted[0].objectName
            var totalPrice = 0.0

            for (sale in listSorted) {
                if (sale.objectName != namePackage) {
                    listPrice.add(ValueDataEntry(namePackage, totalPrice))
                    namePackage = sale.objectName
                    totalPrice = sale.amountDelivered
                } else {
                    val price = if (sale.currency == "USD") {
                        convertDollarToEuros(sale.amountDelivered)
                    } else {
                        sale.amountDelivered
                    }
                    totalPrice += price
                }
            }
            listPrice.add(ValueDataEntry(namePackage, totalPrice))
            return listPrice
        }


        fun anyChartPackageNB(list: List<Sale>): MutableList<DataEntry> {
            val listSorted = Utils.sortSalesByPackage(list)
            val listEntry = mutableListOf<DataEntry>()
            var nbPerPackage = 0
            var namePackage = listSorted[0].objectName

            for (sale in listSorted) {
                if (sale.objectName != namePackage) {
                    listEntry.add(ValueDataEntry(namePackage, nbPerPackage))
                    nbPerPackage = 1
                    namePackage = sale.objectName
                } else {
                    nbPerPackage++
                }

                if (listSorted.indexOf(sale) == listSorted.size - 1) {
                    listEntry.add(ValueDataEntry(namePackage, nbPerPackage))
                }
            }

            return listEntry

        }

        fun graphAnyChartMap(list: List<Sale>): MutableList<DataEntry> {
            val listSorted = Utils.sortByCountry(list)
            val listEntry = mutableListOf<DataEntry>()
            var nbPerPackage = 0
            var country = listSorted[0].countryCode

            for (sale in listSorted) {
                    if (sale.countryCode != country) {
                        listEntry.add(ValueDataEntry(country, nbPerPackage))
                        nbPerPackage = 1
                        country = sale.countryCode
                    } else {
                        nbPerPackage++
                    }
                if (listSorted.indexOf(sale) == listSorted.size - 1) {
                    listEntry.add(ValueDataEntry(country, nbPerPackage))
                }
            }
            return listEntry
        }


        fun getAnyChartBubbleFromFranceAndRussia(list: List<Sale>): MutableList<DataEntry> {
            val listSorted = Utils.sortByCountry(list)
            val listEntry = mutableListOf<DataEntry>()
            var nbFrance = 0
            var nbRussia = 0
            for (sale in listSorted) {
                if (sale.countryCode == "RU") {
                    nbRussia ++
                }
                if (sale.countryCode == "FR") {
                    nbFrance ++
                }
            }

            listEntry.add(DataEntryFranceRussia("FR", "FR", nbFrance, 2.213749, 46.227638))
            listEntry.add(DataEntryFranceRussia("RU", "RU", nbRussia, 105.318756, 61.524010))

            return listEntry
        }

        fun graphAnyChartMapChrono(list: List<Sale>): List<DataEntry> {
            val listSorted = Utils.sortByCountry(list)
            val listEntry = mutableListOf<DataEntry>()
            var nbPerPackage = 0
            var country = listSorted[0].countryCode

            for (sale in listSorted) {
                    if (sale.countryCode != country) {
                        listEntry.add(CustomDataEntry(country, country, nbPerPackage))
                        nbPerPackage = 1
                        country = sale.countryCode
                    } else {
                        nbPerPackage++
                    }

                if (listSorted.indexOf(sale) == listSorted.size - 1) {
                    listEntry.add(CustomDataEntry(country, country, nbPerPackage))
                }
            }
            return listEntry

        }

        fun graphAnyChartMapChronopleth(list: List<Sale>): ChronopletReturn {
            val listSorted = sortByCountry(list)
            val listCountries = getAllCountryCode()
            val listEntry = mutableListOf<DataEntry>()
            var nbPerPackage = 1
            var country = listSorted[0].countryCode
            var maxNb = 0
            var maxNB2 = 0



            for (sale in listSorted) {
                if (sale.countryCode != country) {
                    listEntry.add(ValueDataEntry(country, nbPerPackage))
                    nbPerPackage = 1
                    country = sale.countryCode
                } else {
                    nbPerPackage++
                }
                if (listSorted.indexOf(sale) == listSorted.size - 1) {
                    listEntry.add(ValueDataEntry(country, nbPerPackage))
                }
            }


            for (sale in listSorted) {
                if (sale.countryCode != country) {
                    if(listCountries.contains(country)) listCountries.remove(country)
                        if (nbPerPackage > maxNb)  {
                            maxNB2 = maxNb
                            maxNb = nbPerPackage
                        }
                        listEntry.add(CustomDataEntryChrono(country, nbPerPackage))
                        nbPerPackage = 1
                        country = sale.countryCode

                } else {
                    nbPerPackage++
                }
                if (listSorted.indexOf(sale) == listSorted.size - 1) {
                    if(listCountries.contains(country)) listCountries.remove(country) 
                    if (nbPerPackage > maxNb)  {
                        maxNB2 = maxNb
                        maxNb = nbPerPackage
                    }
                    listEntry.add(CustomDataEntryChrono(country, nbPerPackage))
                }
            }

            for (countryCode in listCountries) {
                listEntry.add(CustomDataEntryChrono(countryCode, 0))
            }

            return ChronopletReturn(listEntry, maxNb, maxNB2)
        }

        private fun getAllCountryCode() : MutableList<String> {
            return mutableListOf(
                "AF","AO","AL","AE","AR","AM",
                "TF","AU","AT","AZ","BI","BE",
                "BJ","BF","BD","BG","BA","BY",
                "BZ","BO","BR","BN","BT","BW",
                "CF","CA","CH","CL","CN","CI",
                "CM","Cyprus_U.N._Buffer_Zone",
                "CD","CG","CO","CR","CU",
                "N._Cyprus","CY","CZ","DE","DJ",
                "DK","DO","DZ","EC","EG","ER",
                "ES","EE","ET","FI","FJ","FR",
                "GA","GB","GE","GH","GN","GW",
                "GQ","GR","GL","GT","GY","HN",
                "HR","HT","HU","ID","IN","IE",
                "IR","IQ","IS","IL","IT","JO",
                "JP","KZ","KE","KG","KH","KR",
                "Kosovo","KW","LA","LB","LR",
                "LY","LK","LS","LT","LV","MA",
                "MD","MG","MX","MK","ML","MM",
                "ME","MN","MZ","MR","MW","MY",
                "NA","NC","NE","NG","NI","NL",
                "NO","NP","NZ","OM","PK","PA",
                "PE","PH","PG","PL","PR","KP",
                "PT","PY","PS","QA","RO","RW",
                "EH","SA","SD","SS","SN","SL",
                "SV","Somaliland","SO","RS",
                "SR","SK","SI","SE","SZ","SY",
                "TD","TG","TH","TJ","TM","TL",
                "TN","TR","TW","TZ","UG","UA",
                "UY","US","UZ","VE","VN","YE",
                "ZA","ZM","ZW","RU"
            )
        }
    }
}

class CustomDataEntry(val id : String, val name : String, val value : Number) : DataEntry() {
    init {
        setValue("id", id);
        setValue("name", name);
        setValue("value", value);
    }
}

class CustomDataEntryChrono(val id : String, val value : Number) : DataEntry() {
    init {
        setValue("id", id);
        setValue("value", value);
    }
}

class DataEntryFranceRussia(val id : String, val name : String, val value : Number, val longitude : Double, val latitude : Double) : DataEntry() {
    init {
        setValue("id", id)
        setValue("name", name)
        setValue("value", value)
        setValue("lat", latitude)
        setValue("long", longitude)
    }
}
