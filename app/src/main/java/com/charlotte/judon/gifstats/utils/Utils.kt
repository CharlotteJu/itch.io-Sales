package com.charlotte.judon.gifstats.utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.*
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        fun convertStringToDate(dateStringFromCSV: String): Date {

            val dateString = dateStringFromCSV.substring(0, 10)
            val hourString = dateStringFromCSV.substring(11, 19)
            val completeDateString = "$dateString $hourString"
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return simpleDateFormat.parse(completeDateString)
        }

        fun convertStringToDateWithLocale(dateStringFromCSV: String, hourString: String, format : String) : List<String> {

            val list = arrayListOf<String>()
            val completeDateString = "$dateStringFromCSV $hourString+0000"
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS]Z")
            val resultOfParsing = OffsetDateTime.parse(completeDateString, dateFormatter)
            val timeZone2 = ZoneId.systemDefault()
            val localeTime = resultOfParsing.atZoneSameInstant(timeZone2)
            val date = Date.from(localeTime.toInstant())

            val formatterDate = SimpleDateFormat(format, Locale.getDefault())
            val dateString = formatterDate.format(date)
            list.add(dateString)
            val formatterTime = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeString = formatterTime.format(date)
            list.add(timeString)

            return list
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
                        .truncatedTo(ChronoUnit.DAYS)) {
                    listToReturn.add(sale)
                }
            }
            return listToReturn
        }

        fun castSaleListInSaleMonthList(salesList: List<Sale>): List<MonthSale> {
            val listToReturn = arrayListOf<MonthSale>()
            var listForOneMonth = arrayListOf<Sale>()

            val dateOfToday = Date().toString()
            val monthOfToday = dateOfToday.substring(4, 7)
            val yearOfToday = dateOfToday.substring(dateOfToday.length -4)
            if (salesList.isNotEmpty()) {
                val refDay = salesList[0].dateDate.toString()
                var refMonth = refDay.substring(4, 7)
                var refYear = refDay.substring(refDay.length-4)
                for (sale in salesList) {
                    val dayTemp = sale.dateDate.toString()
                    val monthTemp = dayTemp.substring(4, 7)
                    val yearTemp = dayTemp.substring(dayTemp.length-4)
                    if (monthTemp == refMonth && yearTemp == refYear) {
                        listForOneMonth.add(sale)
                    } else {
                        val total = calculTotalNetSales(listForOneMonth)
                        val bool = (refMonth == monthOfToday && refYear == yearOfToday)
                        listToReturn.add(
                            MonthSale(
                                refMonth,
                                refYear,
                                listForOneMonth.size,
                                total,
                                bool,
                                listForOneMonth
                            )
                        )
                        refMonth = monthTemp
                        refYear = yearTemp
                        listForOneMonth = arrayListOf(sale)
                    }

                    if (salesList.indexOf(sale) == salesList.size - 1) {
                        val total = calculTotalNetSales(listForOneMonth)
                        val bool = (monthTemp == monthOfToday && yearTemp == yearOfToday)
                        listToReturn.add(
                            MonthSale(
                                monthTemp,
                                yearTemp,
                                listForOneMonth.size,
                                total,
                                bool,
                                listForOneMonth
                            )
                        )
                    }
                }
            }
            return listToReturn.reversed()
        }

        fun convertDollarToEuros(price: Double): Double {
            val decimalFormat = DecimalFormat("####0.00")
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'
            decimalFormat.decimalFormatSymbols = separator
            return decimalFormat.format(price * 0.83).toDouble()
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
                    sale.amount
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

        ///////////////////////////////////////// MPBAR /////////////////////////////////////////

        fun calculateDiffBetweenTwoDates(dateOfReference : Date, dateSale : Date) : CompareDates {
            val calendarToCompare = Calendar.getInstance()
            calendarToCompare.time = dateOfReference
            val dayOfReference = calendarToCompare.get(Calendar.DAY_OF_YEAR)
            val yearOfReference = calendarToCompare.get(Calendar.YEAR)
            calendarToCompare.add(Calendar.DAY_OF_YEAR, 1)
            val datePlus1 = calendarToCompare.time
            val dayPlus1 = calendarToCompare.get(Calendar.DAY_OF_YEAR)
            val yearPlus1 = calendarToCompare.get(Calendar.YEAR)

            calendarToCompare.time = dateSale
            val dayTemp = calendarToCompare.get(Calendar.DAY_OF_YEAR)
            val yearTemp = calendarToCompare.get(Calendar.YEAR)

            return if (dayTemp == dayOfReference && yearTemp == yearOfReference) {
                CompareDates.SAME
            } else if (dayTemp == dayPlus1 && yearTemp == yearPlus1) {
                CompareDates.PLUS_ONE
            } else if (dayTemp > dayPlus1 || yearTemp >= yearPlus1) {
                CompareDates.PLUS_OTHER
            } else {
                CompareDates.ERROR
            }
        }

        fun calculateNumberOfDaysOfDifference(dateOfReference : Date, dateSale : Date) : Long {
            val difLong = dateSale.time - dateOfReference.time
            var difInt = TimeUnit.DAYS.convert(difLong, TimeUnit.MILLISECONDS)
            val simpleDataFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val hourDay = simpleDataFormatter.format(dateOfReference)
            val hourDateTemp = simpleDataFormatter.format(dateSale)

            if(hourDay > hourDateTemp) {
                difInt ++
            }

            return difInt
        }

        fun graphMPByDay(salesList: List<Sale>)  : MpBarReturn {

            val listSorted = sortSalesByDate(salesList)
            val listEntry = arrayListOf<BarEntry>()
            val listString = arrayListOf<String>()
            val formatterDate = SimpleDateFormat("dd/MM", Locale.getDefault())
            var nbPerDay = 0f

            if(listSorted.isEmpty()) {
                return MpBarReturn(null)
            }

            var dateOfReference = listSorted[0].dateDate

            for (sale in listSorted) {
                val dateTemp = sale.dateDate
                var isLastAdding = false

                val diffDates = calculateDiffBetweenTwoDates(dateOfReference, dateTemp)

                when (diffDates){
                    CompareDates.SAME -> {
                        nbPerDay++
                    }
                    CompareDates.PLUS_ONE -> {
                        val dateString = formatterDate.format(dateOfReference)
                        listEntry.add(BarEntry(listString.size.toFloat(), nbPerDay))
                        listString.add(dateString)
                        dateOfReference = dateTemp
                        nbPerDay = 1f
                        isLastAdding = true
                    }
                    CompareDates.PLUS_OTHER -> {
                        val dateString = formatterDate.format(dateOfReference)
                        listEntry.add(BarEntry(listString.size.toFloat(), nbPerDay))
                        listString.add(dateString)

                        val difInt = calculateNumberOfDaysOfDifference(dateOfReference, dateTemp)
                        for (i in 0 until difInt -1) {
                            val calendarTemp = Calendar.getInstance()
                            calendarTemp.time = dateOfReference
                            calendarTemp.add(Calendar.DAY_OF_YEAR, i.toInt()+1)
                            val dateStringTemp = formatterDate.format(calendarTemp.time)
                            listEntry.add(BarEntry(listString.size.toFloat(), 0f))
                            listString.add(dateStringTemp)
                        }

                        dateOfReference = dateTemp
                        nbPerDay = 1f
                        isLastAdding = true
                    }
                    CompareDates.ERROR -> {
                    //TODO
                    }
                }

                if (listSorted.indexOf(sale) == listSorted.size - 1 && !isLastAdding) {
                    val dateString = formatterDate.format(dateOfReference)
                    listEntry.add(BarEntry(listString.size.toFloat(), nbPerDay))
                    listString.add(dateString)
                }
            }

            val calendarNow = Calendar.getInstance()
            val timeNow = calendarNow.time

            if(dateOfReference.time != timeNow.time) {
                val difLong = timeNow.time - dateOfReference.time
                val difInt = TimeUnit.DAYS.convert(difLong, TimeUnit.MILLISECONDS)

                for (i in 0 until difInt) {
                    val calendarTemp = Calendar.getInstance()
                    calendarTemp.time = dateOfReference
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
            return (MpBarReturn(barDataSet, listString))
        }

        fun graphMPBarByHour(salesList: List<Sale>) : MpBarReturn {
            val listEntry = arrayListOf<BarEntry>()
            val listHours = arrayListOf<HourList>()

            if(salesList.isEmpty()) {
                return MpBarReturn(null)
            }

            for (i in 0 .. 23) {
                listHours.add(HourList(i))
            }

            for (sale in salesList) {
                val date = convertStringToDateWithLocale(sale.dateString, sale.hour, "MM/dd/yyyy")
                val hourString = date[1].substring(0, 2)
                val hourSale = hourString.toInt()
                listHours.get(hourSale).nb ++
            }

            for (hourItem in listHours) {
                listEntry.add(BarEntry(hourItem.hour.toFloat(), hourItem.nb.toFloat()))
            }

            val barDataSet = BarDataSet(listEntry, " ")
            barDataSet.color = Color.parseColor("#F80039")
            barDataSet.highLightColor = Color.parseColor("#4B2E5A")
            barDataSet.setDrawValues(false)
            return MpBarReturn(barDataSet)

        }

        fun graphMPBarByDayOfWeek(list: List<Sale>) : MpBarReturn {

            if (list.isEmpty()) {
                return MpBarReturn(null)
            }

            var nbMonday = 0
            var nbTuesday = 0
            var nbWednesday = 0
            var nbThursday = 0
            var nbFriday = 0
            var nbSaturday = 0
            var nbSunday = 0

            val listDay = arrayListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

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
                BarEntry(6f, nbSunday.toFloat()))

            val barDataSet = BarDataSet(listBarEntry, " ")
            barDataSet.color = Color.parseColor("#F80039")
            barDataSet.highLightColor = Color.parseColor("#4B2E5A")
            barDataSet.setDrawValues(false)
            return MpBarReturn(barDataSet, listDay)
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
            val listHours = arrayListOf<HourList>()

            for (i in 0 .. 23) {
                listHours.add(HourList(i))
            }

            for (sale in listSorted) {
                val date = convertStringToDateWithLocale(sale.dateString, sale.hour, "MM/dd/yyyy")
                val hourString = date[1].substring(0, 2)
                val hourSale = hourString.toInt()

                listHours.get(hourSale).nb ++
            }

            for (hourItem in listHours) {
                listEntry.add(BarEntry(hourItem.hour.toFloat(), hourItem.nb.toFloat()))
            }

            var name = ""
            val listDay = arrayListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
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



        ///////////////////////////////////////// ANYCHART /////////////////////////////////////////

        fun anyChartPackagePrice(list: List<Sale>, currentCurrency : CustomCurrency, listCurrencies : List<CustomCurrency>): MutableList<DataEntry> {
            val listSorted = sortSalesByPackage(list)
            val listPrice = mutableListOf<DataEntry>()

            if (listSorted.isNotEmpty()) {
                var namePackage = listSorted[0].objectName
                var totalPrice = 0.0

                for (sale in listSorted) {
                    if (sale.objectName != namePackage) {
                        listPrice.add(ValueDataEntry(namePackage, totalPrice))
                        namePackage = sale.objectName
                        totalPrice = sale.amountDelivered
                    } else {
                        val price = if(sale.currency == currentCurrency.code) {
                            sale.amountDelivered
                        } else {
                            UtilsCurrency.convertPriceInTwoCurrencies(sale.currency, currentCurrency, sale.amountDelivered, listCurrencies)
                        }
                        totalPrice += price
                    }
                }
                listPrice.add(ValueDataEntry(namePackage, totalPrice))
            }
            return listPrice
        }


        fun anyChartPackageNB(list: List<Sale>): MutableList<DataEntry> {
            val listSorted = sortSalesByPackage(list)
            val listEntry = mutableListOf<DataEntry>()
            var nbPerPackage = 0
            if (listSorted.isEmpty()) {
                return listEntry
            } else {
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
        }


        fun graphAnyChartMapChronopleth(list: List<Sale>): ChronopletReturn {
            val listSorted = sortByCountry(list)
            val listCountries = getAllCountryCode()
            val listEntry = mutableListOf<DataEntry>()
            val listCountry = mutableListOf<Country>()
            var nbPerPackage = 1
            var maxNb = 0
            var maxNB2 = 0
            var country = ""
            if(listSorted.isNotEmpty()) {
                country = listSorted[0].countryCode
            }
            for (sale in listSorted) {
                if (sale.countryCode != country) {
                    if(listCountries.contains(country)) listCountries.remove(country)
                        if (nbPerPackage > maxNb)  {
                            maxNB2 = maxNb
                            maxNb = nbPerPackage
                        }
                        listCountry.add(Country(country, nbPerPackage))
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
                    listCountry.add(Country(country, nbPerPackage))
                    listEntry.add(CustomDataEntryChrono(country, nbPerPackage))
                }
            }

            for (countryCode in listCountries) {
                listCountry.add(Country(countryCode, 0))
                listEntry.add(CustomDataEntryChrono(countryCode, 0))
            }

            listCountry.sortBy { country ->  country.nb}
            listCountry.reverse()

            return ChronopletReturn(listEntry, maxNb, maxNB2, listCountry)
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

        fun getRanges(nbMax : Int, nbMax2 : Int) : Array<String> {
            var nb1 = 0
            var nb2 = 0
            var nb3 = 0
            var nb4 = 0

            if (nbMax<10) {
                return arrayOf("{less: 1}", "{greater: 1}")
            }

            val diffMax = nbMax - nbMax2

            if (diffMax > nbMax/10) {
                nb1 = nbMax2/4
                nb2 = (nbMax2/4) *2
                nb3 = (nbMax2/4) *3
                nb4 = nbMax2
                Log.d("DEBUG_APP", "BEAUCOUP DE DIFFERENCE : 1 : $nb1 //// 2 : $nb2 //// 3 : $nb3 //// 4 : $nb4 ")
            }
            else {
                nb1 = nbMax/5
                nb2 = (nbMax/5) *2
                nb3 = (nbMax/5) *3
                nb4 = (nbMax/4) *3
                Log.d("DEBUG_APP", "PETITE DIFFERENCE : 1 : $nb1 //// 2 : $nb2 //// 3 : $nb3 //// 4 : $nb4 ")
            }

            return arrayOf("{less: 1}", "{from: 1, to: $nb1}", "{from: $nb1, to: $nb2}", "{from: $nb2, to: $nb3}", "{from: $nb3, to: $nb4}", "{greater: $nb4}")
        }
    }
}



class CustomDataEntryChrono(val id : String, val value : Number) : DataEntry() {
    init {
        setValue("id", id);
        setValue("value", value);
    }
}

enum class CompareDates() {
    SAME,
    PLUS_ONE,
    PLUS_OTHER,
    ERROR
}




/*

class DataEntryFranceRussia(val id : String, val name : String, val value : Number, val longitude : Double, val latitude : Double) : DataEntry() {
    init {
        setValue("id", id)
        setValue("name", name)
        setValue("value", value)
        setValue("lat", latitude)
        setValue("long", longitude)
    }
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


 */
