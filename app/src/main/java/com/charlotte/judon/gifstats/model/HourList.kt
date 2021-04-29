package com.charlotte.judon.gifstats.model

/**
 * Data class used by [/utils/UtilsCharts.kt] to populate the Chart in [fragments/DateDetailFragment.kt] from the library [MPAndroidChart : https://github.com/PhilJay/MPAndroidChart]
 * @param hour : Int for day's hour
 * @param nb : Int for sale's number in this hour
 * @author Charlotte JUDON
 */
data class HourList(val hour : Int, var nb : Int = 0)