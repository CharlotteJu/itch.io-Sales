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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UtilsCharts {

    companion object {
        ///////////////////////////////////////// MPBAR /////////////////////////////////////////

        fun graphMPByDay(salesList: List<Sale>, format : String)  : MpBarReturn
        {
            val listSorted = UtilsGeneral.sortSalesByDate(salesList)

            if(listSorted.isEmpty()) {
                return MpBarReturn(null)
            }

            val listEntry = arrayListOf<BarEntry>()
            val listString = arrayListOf<String>()
            val formatterDate = SimpleDateFormat(format, Locale.getDefault())
            var nbPerDay = 0f

            if(listSorted.isEmpty()) {
                return MpBarReturn(null)
            }

            var dateOfReference = listSorted[0].dateDate

            for (sale in listSorted) {
                val dateTemp = sale.dateDate
                var isLastAdding = false

                val diffDates = UtilsGeneral.calculateDiffBetweenTwoDates(dateOfReference, dateTemp)

                when (diffDates){
                    UtilsGeneral.CompareDates.SAME -> {
                        nbPerDay++
                    }
                    UtilsGeneral.CompareDates.PLUS_ONE -> {
                        val dateString = formatterDate.format(dateOfReference)
                        listEntry.add(BarEntry(listString.size.toFloat(), nbPerDay))
                        listString.add(dateString)
                        dateOfReference = dateTemp
                        nbPerDay = 1f
                        isLastAdding = true
                    }
                    UtilsGeneral.CompareDates.PLUS_OTHER -> {
                        val dateString = formatterDate.format(dateOfReference)
                        listEntry.add(BarEntry(listString.size.toFloat(), nbPerDay))
                        listString.add(dateString)

                        val difInt = UtilsGeneral.calculateNumberOfDaysOfDifference(dateOfReference, dateTemp)
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
                    UtilsGeneral.CompareDates.ERROR -> {}
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

            val barDataSet = BarDataSet(listEntry, "")
            barDataSet.color = Color.parseColor("#FA5C5C")
            barDataSet.highLightColor = Color.parseColor("#4B2E5A")
            barDataSet.setDrawValues(false)
            return (MpBarReturn(barDataSet, listString))
        }

        private fun getDataForGraphByHour(salesList: List<Sale>) : List<BarEntry>{
            val listEntry = arrayListOf<BarEntry>()
            val listHours = arrayListOf<HourList>()

            if(salesList.isEmpty()) {
                return listEntry
            }

            for (i in 0 .. 23) {
                listHours.add(HourList(i))
            }

            for (sale in salesList) {
                val date = UtilsGeneral.convertStringToDateWithLocale(sale.dateString, sale.hour, "MM/dd/yyyy")
                val hourString = date[1].substring(0, 2)
                val hourSale = hourString.toInt()
                listHours[hourSale].nb ++
            }

            for (hourItem in listHours) {
                listEntry.add(BarEntry(hourItem.hour.toFloat(), hourItem.nb.toFloat()))
            }

            return listEntry
        }

        fun graphMPBarByHour(listSales : List<Sale>) : MpBarReturn {
            if(listSales.isEmpty()) {
                return MpBarReturn(null)
            }
            val listEntry = getDataForGraphByHour(listSales)
            val barDataSet = BarDataSet(listEntry, " ")
            barDataSet.color = Color.parseColor("#FA5C5C")
            barDataSet.highLightColor = Color.parseColor("#4B2E5A")
            barDataSet.setDrawValues(false)
            return MpBarReturn(barDataSet)
        }

        private fun getDataForGraphByDayOfWeek(list: List<Sale>) : List<BarEntry> {
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
            val listBarEntry = arrayListOf(
                BarEntry(0f, nbMonday.toFloat()),
                BarEntry(1f, nbTuesday.toFloat()),
                BarEntry(2f, nbWednesday.toFloat()),
                BarEntry(3f, nbThursday.toFloat()),
                BarEntry(4f, nbFriday.toFloat()),
                BarEntry(5f, nbSaturday.toFloat()),
                BarEntry(6f, nbSunday.toFloat())
            )

            return listBarEntry

        }

        fun graphMPBarByDayOfWeek(list: List<Sale>) : MpBarReturn {
            if (list.isEmpty()) {
                return MpBarReturn(null)
            }
            val listDay = arrayListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val listBarEntry = getDataForGraphByDayOfWeek(list)

            val barDataSet = BarDataSet(listBarEntry, " ")
            barDataSet.color = Color.parseColor("#FA5C5C")
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

        private fun getDataForGraphByDaysWithHours(listSorted: List<Sale>) : List<BarEntry> {
            val listEntry = arrayListOf<BarEntry>()
            val listHours = arrayListOf<HourList>()

            for (i in 0 .. 23) {
                listHours.add(HourList(i))
            }

            for (sale in listSorted) {
                val date = UtilsGeneral.convertStringToDateWithLocale(sale.dateString, sale.hour, "MM/dd/yyyy")
                val hourString = date[1].substring(0, 2)
                val hourSale = hourString.toInt()

                listHours[hourSale].nb ++
            }

            for (hourItem in listHours) {
                listEntry.add(BarEntry(hourItem.hour.toFloat(), hourItem.nb.toFloat()))
            }

            return listEntry
        }

        fun graphMPBarByDayWithHours(list: List<Sale>, day : String, context : Context)  : DateDetail {
            val listSorted = UtilsGeneral.sortSalesByHour(getListForOneDay(list, day))
            val listEntry = getDataForGraphByDaysWithHours(listSorted)
            val listDay = arrayListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val listDayFromResources = context.resources.getStringArray(R.array.list_days)
            var name = ""
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
            val listSorted = UtilsGeneral.sortSalesByPackage(list)
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
                            UtilsCurrency.convertPriceBetweenTwoCurrencies(sale.currency, currentCurrency, sale.amountDelivered, listCurrencies)
                        }
                        totalPrice += price
                    }
                }
                listPrice.add(ValueDataEntry(namePackage, totalPrice))
            }
            return listPrice
        }


        fun anyChartPackageNB(list: List<Sale>): MutableList<DataEntry> {
            val listSorted = UtilsGeneral.sortSalesByPackage(list)
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
            val listSorted = UtilsGeneral.sortByCountry(list)
            val listStringCountries = getAllCountryCode()
            val listEntry = mutableListOf<DataEntry>()
            val listCustomCountry = mutableListOf<CustomCountry>()
            var nbPerPackage = 0
            var maxNb = 0
            var maxNb2 = 0
            var country = ""
            if(listSorted.isNotEmpty()) {
                country = listSorted[0].countryCode
            }
            for (sale in listSorted) {
                if (sale.countryCode != country) {
                    if(listStringCountries.contains(country)) listStringCountries.remove(country)
                    listCustomCountry.add(CustomCountry(country, nbPerPackage))
                    listEntry.add(CustomDataEntryChronopleth(country, nbPerPackage))
                    nbPerPackage = 1
                    country = sale.countryCode

                } else {
                    nbPerPackage++
                }
                if (listSorted.indexOf(sale) == listSorted.size - 1) {
                    if(listStringCountries.contains(country)) listStringCountries.remove(country)
                    listCustomCountry.add(CustomCountry(country, nbPerPackage))
                    listEntry.add(CustomDataEntryChronopleth(country, nbPerPackage))
                }
            }

            for (countryCode in listStringCountries) {
                listCustomCountry.add(CustomCountry(countryCode, 0))
                listEntry.add(CustomDataEntryChronopleth(countryCode, 0))
            }

            listCustomCountry.sortBy { countrySale ->  countrySale.nb}
            listCustomCountry.reverse()

            when {
                listCustomCountry.size >= 2 -> {
                    maxNb = listCustomCountry[0].nb
                    maxNb2 = listCustomCountry[1].nb
                }
                listCustomCountry.size == 1-> {
                    maxNb = listCustomCountry[0].nb
                    maxNb2 = 0
                }
                listCustomCountry.size == 0 -> {
                    maxNb = 0
                    maxNb2 = 0
                }
            }

            return ChronopletReturn(listEntry, maxNb, maxNb2, listCustomCountry)
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

            if (nbMax<10 || nbMax ==0|| nbMax2 ==0) {
                return arrayOf("{less: 1}", "{greater: 1}")
            }

            val diffMax = nbMax - nbMax2

            if (diffMax > nbMax/10) {
                nb1 = nbMax2/4
                nb2 = (nbMax2/4) *2
                nb3 = (nbMax2/4) *3
                nb4 = nbMax2
            }
            else {
                nb1 = nbMax/5
                nb2 = (nbMax/5) *2
                nb3 = (nbMax/5) *3
                nb4 = (nbMax/5) *4
            }

            return arrayOf("{less: 1}", "{from: 1, to: $nb1}", "{from: $nb1, to: $nb2}", "{from: $nb2, to: $nb3}", "{from: $nb3, to: $nb4}", "{greater: $nb4}")
        }
    }

    class CustomDataEntryChronopleth(val id : String, val value : Number) : DataEntry() {
        init {
            setValue("id", id);
            setValue("value", value);
        }
    }

}

        //Just for test the logic
        /*
        fun getDataForGraphByDay(listSorted: List<Sale>, format: String) : List<BarEntry> {
            val listEntry = arrayListOf<BarEntry>()
            val listString = arrayListOf<String>()
            val formatterDate = SimpleDateFormat(format, Locale.getDefault())
            var nbPerDay = 0f

            var dateOfReference = listSorted[0].dateDate

            for (sale in listSorted) {
                val dateTemp = sale.dateDate
                var isLastAdding = false

                val diffDates = UtilsGeneral.calculateDiffBetweenTwoDates(dateOfReference, dateTemp)

                when (diffDates){
                    UtilsGeneral.CompareDates.SAME -> {
                        nbPerDay++
                    }
                    UtilsGeneral.CompareDates.PLUS_ONE -> {
                        val dateString = formatterDate.format(dateOfReference)
                        listEntry.add(BarEntry(listString.size.toFloat(), nbPerDay))
                        listString.add(dateString)
                        dateOfReference = dateTemp
                        nbPerDay = 1f
                        isLastAdding = true
                    }
                    UtilsGeneral.CompareDates.PLUS_OTHER -> {
                        val dateString = formatterDate.format(dateOfReference)
                        listEntry.add(BarEntry(listString.size.toFloat(), nbPerDay))
                        listString.add(dateString)

                        val difInt = UtilsGeneral.calculateNumberOfDaysOfDifference(dateOfReference, dateTemp)
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
                        isLastAdding = false
                    }
                    UtilsGeneral.CompareDates.ERROR -> {}
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

            return listEntry
        }
*/




