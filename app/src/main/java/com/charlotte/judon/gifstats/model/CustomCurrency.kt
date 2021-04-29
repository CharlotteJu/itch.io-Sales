package com.charlotte.judon.gifstats.model

/**
 * Data class used for get a Currency with rate related to USD
 * It's populate from the information of : https://www.floatrates.com/daily/usd.jso
 * @param code : String currency's code name
 * @param rate : Double rate related to USD
 * @param inverseRate : Double rate inverse related to USD
 * @param symbol : String currency's symbol
 * @author Charlotte JUDON
 */

data class CustomCurrency (val code : String, val rate : Double, val inverseRate : Double, val symbol : String)