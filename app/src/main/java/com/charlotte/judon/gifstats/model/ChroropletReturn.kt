package com.charlotte.judon.gifstats.model

import com.anychart.chart.common.dataentry.DataEntry

data class ChroropletReturn(val listEntry: MutableList<DataEntry>, val max : Int, val max2 : Int, val listCustomCountry : MutableList<CustomCountry>)