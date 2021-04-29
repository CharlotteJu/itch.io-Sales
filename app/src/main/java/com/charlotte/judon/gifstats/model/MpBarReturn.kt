package com.charlotte.judon.gifstats.model

import com.github.mikephil.charting.data.BarDataSet

/**
 * Data class used by [/utils/UtilsCharts.kt] to populate the Chart in [fragments/DateDetailFragment.kt] from the library [MPAndroidChart : https://github.com/PhilJay/MPAndroidChart]
 * @param barDataSet : [BarDataSet] with all information for the chart
 * @param listString : List of String to populate XAxis of Chart
 * @author Charlotte JUDON
 */

data class MpBarReturn(val barDataSet: BarDataSet?, val listString : List<String> = arrayListOf())