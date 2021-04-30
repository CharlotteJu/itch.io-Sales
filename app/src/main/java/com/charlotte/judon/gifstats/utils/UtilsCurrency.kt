package com.charlotte.judon.gifstats.utils

import android.content.SharedPreferences
import com.charlotte.judon.gifstats.model.CustomCurrency
import com.google.gson.Gson
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Class with a [Companion] to cast and transform [CustomCurrency]
 * @author Charlotte JUDON
 */
class UtilsCurrency {

    companion object {

        /**
         * @return the List of all [CustomCurrency] after cast by [castStringInCurrency] in String for the [SharedPreferences]
         * @param sharedPreferences : get the SharedPreferences referred by [SHARED_PREFERENCES_CURRENCY]
         */
        fun getListCurrenciesFromSharedPreferences(sharedPreferences: SharedPreferences) : List<CustomCurrency> {
            return if(sharedPreferences.getString(CURRENCY_CAD, null) != null) {
                listOf(
                    castStringInCurrency(sharedPreferences.getString(CURRENCY_USD, null)!!),
                    castStringInCurrency(sharedPreferences.getString(CURRENCY_CAD, null)!!),
                    castStringInCurrency(sharedPreferences.getString(CURRENCY_GBP, null)!!),
                    castStringInCurrency(sharedPreferences.getString(CURRENCY_EUR, null)!!),
                    castStringInCurrency(sharedPreferences.getString(CURRENCY_JPY, null)!!),
                    castStringInCurrency(sharedPreferences.getString(CURRENCY_AUD, null)!!),
                )
            } else {
                getListCurrencyIfNeverInternet()
            }

        }

        /**
         * @return a String casted compared to the [CustomCurrency] using [Gson]
         * @param currency : [CustomCurrency] to cast
         */
        fun castCurrencyInString(currency: CustomCurrency) : String {
            val gson = Gson()
            return gson.toJson(currency)
        }

        /**
         * @return a [CustomCurrency] casted compared to the String recorded in the [SharedPreferences] using [Gson]
         * @param string : String to cast
         * @explications : If none currency was recorded, return USD
         */
        fun castStringInCurrency(string: String?) : CustomCurrency{
            return if(string != null) {
                val gson = Gson()
                gson.fromJson(string, CustomCurrency::class.java)
            } else {
                CustomCurrency("USD", 1.0, 1.0, "$")
            }

        }

        /**
         * @return a List of [CustomCurrency] after cast by [castJsonInCurrency]
         * @param json : [JSONObject] returned by [https://www.floatrates.com/daily/usd.json]
         */
        fun castJsonInListCurrencies(json: JSONObject) : List<CustomCurrency> {
            val usdCurrency = CustomCurrency(CURRENCY_USD.toUpperCase(), 1.0, 1.0, "$")
            return listOf(
                usdCurrency,
                castJsonInCurrency(json, CURRENCY_CAD, "$"),
                castJsonInCurrency(json, CURRENCY_GBP, "£"),
                castJsonInCurrency(json, CURRENCY_EUR, "€"),
                castJsonInCurrency(json, CURRENCY_JPY, "¥"),
                castJsonInCurrency(json, CURRENCY_AUD, "$"),
            )
        }

        /**
         * @return a List of [CustomCurrency] when no internet connection
         */
        private fun getListCurrencyIfNeverInternet() : List<CustomCurrency> {
            return listOf(
                CustomCurrency(CURRENCY_USD.toUpperCase(), 1.0, 1.0, "$"),
                CustomCurrency(CURRENCY_CAD.toUpperCase(), 1.23, 0.81, "$"),
                CustomCurrency(CURRENCY_GBP.toUpperCase(), 0.72, 1.39, "£"),
                CustomCurrency(CURRENCY_EUR.toUpperCase(), 0.83, 1.21, "€"),
                CustomCurrency(CURRENCY_JPY.toUpperCase(), 108.92, 0.01, "¥"),
                CustomCurrency(CURRENCY_AUD.toUpperCase(), 1.28, 0.78, "$"),
            )
        }

        /**
         * @return a [CustomCurrency] after reading of [json]
         * @param json : [JSONObject] returned by [https://www.floatrates.com/daily/usd.json]
         * @param name : String const for each of the 6 currencies used
         * @param symbol : String symbol for each of the 6 currencies used
         */
        private fun castJsonInCurrency(json: JSONObject, name: String, symbol : String) : CustomCurrency {
            val custom = json.getJSONObject(name)
            return CustomCurrency(
                custom.getString("alphaCode"), custom.getDouble("rate"), custom.getDouble("inverseRate"), symbol
            )
        }

        /**
         * @return a Double price after check of the currentCurrency and the sale's currency
         * @param csvCurrency : String of sale's currency
         * @param goalCurrency : [CustomCurrency] for the currentCurrency
         * @param price : Double used for the calculation
         * @param listCurrency : List of [CustomCurrency] to get all the rate and inverseRate
         * @explications : 1st convert the price in USD
         *                 2nd convert the new price in the goal [CustomCurrency]
         */
        fun convertPriceBetweenTwoCurrencies(csvCurrency: String, goalCurrency: CustomCurrency,
                                             price: Double, listCurrency: List<CustomCurrency>) : Double {
            var priceInUSD = price
            if(csvCurrency != "USD"){
                var inverseRate = 0.0
                when(csvCurrency){
                    "CAD" -> inverseRate = listCurrency[1].inverseRate
                    "GBP" -> inverseRate = listCurrency[2].inverseRate
                    "EUR" -> inverseRate = listCurrency[3].inverseRate
                    "JPY" -> inverseRate = listCurrency[4].inverseRate
                    "AUD" -> inverseRate = listCurrency[5].inverseRate
                }

               priceInUSD = convertPriceInUSD(price, inverseRate)
            }

            val decimalFormat = DecimalFormat("####0.00")
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'
            decimalFormat.decimalFormatSymbols = separator
            return decimalFormat.format(priceInUSD * goalCurrency.rate).toDouble()
        }

        /**
         * @return a Double calculated by the inverseRate of a [CustomCurrency]
         * @param price : Double provided by the [model/Sale.amount] or [model/Sale.amountDelivery]
         * @param currencyFactor : Double provided by the [model/CustomCurrency.inverseRate]
         */
        private fun convertPriceInUSD(price: Double, currencyFactor: Double) : Double {
            val decimalFormat = DecimalFormat("####0.00")
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'
            decimalFormat.decimalFormatSymbols = separator
            return decimalFormat.format(price * currencyFactor).toDouble()
        }

    }
}