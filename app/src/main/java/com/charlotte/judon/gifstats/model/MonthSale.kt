package com.charlotte.judon.gifstats.model

data class MonthSale (var month : String, var nbVente : Int, var totalPrice : Double, var isFinish : Boolean, var list: List<Sale>) {
}