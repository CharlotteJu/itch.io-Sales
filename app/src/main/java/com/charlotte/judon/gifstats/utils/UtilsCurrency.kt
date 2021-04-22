package com.charlotte.judon.gifstats.utils

import android.content.Context
import android.content.SharedPreferences
import com.charlotte.judon.gifstats.model.CustomCurrency
import com.google.gson.Gson
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class UtilsCurrency {

    companion object {

        fun getListCurrenciesFromSharedPreferences(sharedPreferences: SharedPreferences) : List<CustomCurrency> {
            return listOf(
                castStringInCurrency(sharedPreferences.getString(CURRENCY_USD, null)!!),
                castStringInCurrency(sharedPreferences.getString(CURRENCY_CAD, null)!!),
                castStringInCurrency(sharedPreferences.getString(CURRENCY_GBP, null)!!),
                castStringInCurrency(sharedPreferences.getString(CURRENCY_EUR, null)!!),
                castStringInCurrency(sharedPreferences.getString(CURRENCY_JPY, null)!!),
                castStringInCurrency(sharedPreferences.getString(CURRENCY_AUD, null)!!),
            )
        }

        fun castJsonInListCurrencies(json : JSONObject) : List<CustomCurrency> {
            val usdCurrency = CustomCurrency("USD", 1.0, 1.0, "$")
            return listOf(
                usdCurrency,
                castJsonInCurrency(json, CURRENCY_CAD),
                castJsonInCurrency(json, CURRENCY_GBP),
                castJsonInCurrency(json, CURRENCY_EUR),
                castJsonInCurrency(json, CURRENCY_JPY),
                castJsonInCurrency(json, CURRENCY_AUD),
            )
        }

        private fun castJsonInCurrency(json : JSONObject, name : String) : CustomCurrency {
            val custom = json.getJSONObject(name)
            val currency = Currency.getInstance(name)
            return CustomCurrency(custom.getString("alphaCode"), custom.getDouble("rate"), custom.getDouble("inverseRate"), currency.symbol)
        }

        fun castCurrencyInString(currency : CustomCurrency) : String{
            val gson = Gson()
            return gson.toJson(currency)
        }

        fun castStringInCurrency(string : String) : CustomCurrency{
            val gson = Gson()
            return gson.fromJson(string, CustomCurrency::class.java)
        }

        fun convertPriceInTwoCurrencies(csvCurrency : String, goalCurrency : CustomCurrency,price : Double, listCurrency: List<CustomCurrency>) : Double {
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

        private fun convertPriceInUSD(price : Double, currencyFactor : Double) : Double {
            val decimalFormat = DecimalFormat("####0.00")
            val separator = DecimalFormatSymbols()
            separator.decimalSeparator = '.'
            decimalFormat.decimalFormatSymbols = separator
            return decimalFormat.format(price * currencyFactor).toDouble()
        }

    }
}