package com.charlotte.judon.gifstats.model

import com.anychart.chart.common.dataentry.DataEntry
/**
 * Data class used by [/utils/UtilsCharts.kt] to populate the Map from the library [AnyChart : https://github.com/AnyChart/AnyChart-Android]
 * @param listEntry : List of [DataEntry]
 * @param max : Int for the max sale's number of all the countries
 * @param max2 : Int for the 2nd max sale's number of all the countries
 * @param listCustomCountry : List of [CustomCountry]
 * @author Charlotte JUDON
 */
data class ChroropletReturn(val listEntry: MutableList<DataEntry>, val max : Int, val max2 : Int, val listCustomCountry : MutableList<CustomCountry>)