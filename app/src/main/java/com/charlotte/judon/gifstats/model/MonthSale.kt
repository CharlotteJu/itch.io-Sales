package com.charlotte.judon.gifstats.model

/**
 * Data class used by [/utils/UtilsGeneral.kt] to populate the Chart in [fragments/ListMonthFragment.kt]
 * @param month : String of month's name
 * @param year : String of year
 * @param nbSales : Int for the sale's number in this month
 * @param totalPrice : Double for sale's net total price
 * @param isInProgress : Boolean to know if is the current month
 * @param list : List of [Sale]
 * @author Charlotte JUDON
 */
data class MonthSale (var month : String, var year : String, var nbSales : Int, var totalPrice : Double, var isInProgress : Boolean, var list: List<Sale>)