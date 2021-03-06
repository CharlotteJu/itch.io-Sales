package com.charlotte.judon.gifstats.utils

import androidx.fragment.app.Fragment
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.*
import com.charlotte.judon.gifstats.views.fragments.HomeFragment
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Class with a [Companion] to give general information
 * @author Charlotte JUDON
 */
class UtilsGeneral {

    companion object {

        /**
         * Go to HomeFragment if late init var are not initialized
         */
        fun goBackToHomeFragment(fragment: Fragment) {
            fragment.parentFragmentManager.apply {
                val homeFragment = HomeFragment()
                val ft = this.beginTransaction()
                ft.replace(R.id.container, homeFragment).addToBackStack(BACKSTACK).commit()
            }
        }

        /**
         * @return a [Date] converted from the CSV's String using [SimpleDateFormat]
         * @param dateStringFromCSV : String extracted from the CSV
         */
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        fun convertStringToDate(dateStringFromCSV: String): Date {
            val dateString = dateStringFromCSV.substring(0, 10)
            val hourString = dateStringFromCSV.substring(11, 19)
            val completeDateString = "$dateString $hourString"
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return simpleDateFormat.parse(completeDateString)
        }

        /**
         * @return a List of String : list[0] = date // list.[1] = hour
         *           It's is converted to match with the User's time zone
         * @param dateStringFromCSV : String of sale.dateString
         * @param hourString : String of sale.hour
         * @param format : String format based on "MM/dd/yyyy" for [views/fragments/GraphsFragment]
         *                  or on User's Preferences for [views/fragments/ListSalesFragment]
         * @explications : 1st Convert [dateStringFromCSV] and [hourString] on a Date with a [OffsetDateTime]
         *                     to get the sale's hour according to User's time zone
         *                 2nd Convert this date in String to facility the using
         */
        fun convertStringToDateWithLocale(dateStringFromCSV: String,
            hourString: String, format: String): List<String> {

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

        /**
         * @return a Date calculated from [daysAgo]
         * @param daysAgo : Int to know the number of days
         * @explication : Take the today's date from with [daysAgo] is subtracted
         * It is using to take the last 7 or 30 days
         */
        fun getDateStartToFilter(daysAgo: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            return calendar.time
        }

        /**
         * @return a List of [MonthSale] to populate a Recycler view in [views/fragments/ListMonthFragment]
         * @param salesList : List of all [Sale]
         * @param currentCurrency : [CustomCurrency] from the User's preferences
         * @param listCurrencies : List of [CustomCurrency] to have all the rates changes
         * @explications : The sales are filtered by their Date (month and year)
         */
        fun castSaleListInSaleMonthList(salesList: List<Sale>, currentCurrency: CustomCurrency,
                                        listCurrencies: List<CustomCurrency>): List<MonthSale> {
            val listToReturn = arrayListOf<MonthSale>()
            var listForOneMonth = arrayListOf<Sale>()

            val dateOfToday = Date().toString()
            val monthOfToday = dateOfToday.substring(4, 7)
            val yearOfToday = dateOfToday.substring(dateOfToday.length - 4)
            if (salesList.isNotEmpty()) {
                val refDay = salesList[0].dateDate.toString()
                var refMonth = refDay.substring(4, 7)
                var refYear = refDay.substring(refDay.length - 4)
                for (sale in salesList) {
                    val dayTemp = sale.dateDate.toString()
                    val monthTemp = dayTemp.substring(4, 7)
                    val yearTemp = dayTemp.substring(dayTemp.length - 4)
                    if (monthTemp == refMonth && yearTemp == refYear) {
                        listForOneMonth.add(sale)
                    } else {
                        val total =
                            calculationTotalNetSales(listForOneMonth, currentCurrency, listCurrencies)
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
                        val total =
                            calculationTotalNetSales(listForOneMonth, currentCurrency, listCurrencies)
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


        /**
         * @return Double for the [Sale]'s net total price
         * @param list : List of [Sale]
         * @param currentCurrency : [CustomCurrency] from the User's preferences
         * @param listCurrencies : List of [CustomCurrency] to have all the rates changes
         */
        fun calculationTotalNetSales(list: List<Sale>, currentCurrency: CustomCurrency,
                                     listCurrencies: List<CustomCurrency>): Double {
            var total = 0.0
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'
            for (sale in list) {
                val price = if (sale.currency == currentCurrency.code) {
                    sale.amountDelivered
                } else {
                    UtilsCurrency.convertPriceBetweenTwoCurrencies(
                        sale.currency,
                        currentCurrency,
                        sale.amountDelivered,
                        listCurrencies
                    )
                }
                total += price
            }
            val decimal = BigDecimal(total).setScale(2, RoundingMode.HALF_UP)
            return decimal.toDouble()
        }

        /**
         * @return Double for the [Sale]'s gross total price
         * @param list : List of [Sale]
         * @param currentCurrency : [CustomCurrency] from the User's preferences
         * @param listCurrencies : List of [CustomCurrency] to have all the rates changes
         */
        fun calculationTotalGrossSales(list: List<Sale>, currentCurrency: CustomCurrency,
                                       listCurrencies: List<CustomCurrency>): Double {
            var total = 0.0
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'

            for (sale in list) {
                val price = if (sale.currency == currentCurrency.code) {
                    sale.amount
                } else {
                    UtilsCurrency.convertPriceBetweenTwoCurrencies(
                        sale.currency,
                        currentCurrency,
                        sale.amount,
                        listCurrencies
                    )
                }
                total += price
            }
            val decimal = BigDecimal(total).setScale(2, RoundingMode.HALF_UP)
            return decimal.toDouble()
        }

        /**
         * @return a Double calculated from [brut] / [net] to have % of charges
         * @param brut : Double provided by [calculationTotalGrossSales]
         * @param net : Double provided by [calculationTotalNetSales]
         */
        fun calculChargesSales(brut: Double, net: Double): Double {
            val decimalFormat = DecimalFormat("####0.00")
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'
            decimalFormat.decimalFormatSymbols = separator

            val total = ((brut - net) / brut) * 100

            return decimalFormat.format(total).toDouble()
        }

        /**
         * @return [CompareDates] : State to know if 2 dates are the same, +1 or more used by [UtilsCharts.getMPChartByDay]
         * @param dateOfReference : [Date] of the last sale
         * @param dateSale : [Date] of the next sale
         * @link [CompareDates]
         */
        fun calculateDiffBetweenTwoDates(dateOfReference : Date, dateSale : Date) : CompareDates {
            val calendarToCompare = Calendar.getInstance()
            calendarToCompare.time = dateOfReference
            val dayOfReference = calendarToCompare.get(Calendar.DAY_OF_YEAR)
            val yearOfReference = calendarToCompare.get(Calendar.YEAR)
            calendarToCompare.add(Calendar.DAY_OF_YEAR, 1)
            calendarToCompare.time
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

        /**
         * @return Long : Number of days without 2 sales used by [UtilsCharts.getMPChartByDay] when [CompareDates.PLUS_OTHER]
         * @param dateOfReference : [Date] of the last sale
         * @param dateSale : [Date] of the next sale
         * @link [CompareDates]
         */
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


        /**
         * @return a List of [Sale] from a specific [Date]
         * @param list : List of [Sale]
         * @param dateStart : Specific [Date]
         */
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

        fun sortByCountry(list: List<Sale>): List<Sale> {
            return list.sortedWith(compareBy { it.countryCode })
        }

        fun sortSalesByDate(list: List<Sale>): List<Sale> {
            return list.sortedWith(compareBy { it.dateDate })
        }

        fun sortSalesByHour(list: List<Sale>): List<Sale> {
            return list.sortedWith(compareBy { it.hour })
        }

        fun sortSalesByPackage(list: List<Sale>): List<Sale> {
            return list.sortedWith(compareBy { it.objectName })
        }
    }
    /**
     * Enum class used to compare Dates in [calculateDiffBetweenTwoDates]
     */
    enum class CompareDates {
        SAME,
        PLUS_ONE,
        PLUS_OTHER,
        ERROR
    }
}

