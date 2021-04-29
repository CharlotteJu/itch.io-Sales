package com.charlotte.judon.gifstats.model

import com.github.mikephil.charting.data.BarEntry

/**
 * Data class used by [/utils/UtilsCharts.kt] to populate the Map from the library [AnyChart : https://github.com/AnyChart/AnyChart-Android]
 * @param code : String currency's code name
 * @param rate : Double rate related to USD
 * @param inverseRate : Double rate inverse related to USD
 * @param symbol : String currency's symbol
 * @author Charlotte JUDON
 */
class DateDetail(var day : String, var listEntry : List<BarEntry>, var totalSale : Int)