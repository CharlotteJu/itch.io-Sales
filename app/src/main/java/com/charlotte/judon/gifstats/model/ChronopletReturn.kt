package com.charlotte.judon.gifstats.model

import com.anychart.chart.common.dataentry.DataEntry

data class ChronopletReturn(val listEntry: MutableList<DataEntry>, val max : Int, val max2 : Int, val listCountry : MutableList<Country>) {
}