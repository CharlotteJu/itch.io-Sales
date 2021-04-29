package com.charlotte.judon.gifstats.model

/**
 * Data class used by [/utils/UtilsCharts.kt] to populate the Map from the library [AnyChart : https://github.com/AnyChart/AnyChart-Android]
 * @param country : String country's code name
 * @param nb : Int for sale's number
 * @author Charlotte JUDON
 */
data class CustomCountry(val country : String, val nb : Int)