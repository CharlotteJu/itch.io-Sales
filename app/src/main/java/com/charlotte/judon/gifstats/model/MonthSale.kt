package com.charlotte.judon.gifstats.model

data class MonthSale (var month : String, var year : String, var nbVente : Int, var totalPrice : Double, var isInProgress : Boolean, var list: List<Sale>) {
}