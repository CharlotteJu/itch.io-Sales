package com.charlotte.judon.gifstats.views.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.CustomCurrency
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.*
import com.charlotte.judon.gifstats.viewModel.ViewModel
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    private lateinit var viewModel : ViewModel
    private lateinit var listSales : List<Sale>
    private lateinit var mView : View
    private var currency : String? = null
    private lateinit var currentCurrency : CustomCurrency
    private var dateFormat : String? = null
    private lateinit var listCurrencies : List<CustomCurrency>

    companion object {

        @JvmStatic
        fun newInstance(viewModel: ViewModel, listSales : List<Sale>) : SettingsFragment
        {
            return SettingsFragment()
                .apply {
                    this.viewModel = viewModel
                    this.listSales = listSales
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_settings, container, false)

        getSharedPreferences()
        populateBtnWithSP()

        mView.radio_group_currency.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.btn_usd -> currency = UtilsCurrency.castCurrencyInString(listCurrencies[0])
                R.id.btn_cad -> currency = UtilsCurrency.castCurrencyInString(listCurrencies[1])
                R.id.btn_gbp -> currency = UtilsCurrency.castCurrencyInString(listCurrencies[2])
                R.id.btn_eur -> currency = UtilsCurrency.castCurrencyInString(listCurrencies[3])
                R.id.btn_jpy -> currency = UtilsCurrency.castCurrencyInString(listCurrencies[4])
                R.id.btn_aud -> currency = UtilsCurrency.castCurrencyInString(listCurrencies[5])
            }
            editCurrencySP()
        }

        mView.radio_group_date_format.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btn_date_us -> dateFormat = US_DATE_FORMAT
                R.id.btn_date_fr -> dateFormat = FR_DATE_FORMAT
            }
            editDateFormatSP()
        }

        mView.button_delete.setOnClickListener {
            viewModel.deleteAllSales(requireContext(), listSales)
        }
        return mView
    }

    private fun editCurrencySP(){
        val sharedCurrency = requireContext().getSharedPreferences(SHARED_PREFERENCES_CURRENCY, Context.MODE_PRIVATE)
        sharedCurrency.edit().putString(KEY_CURRENT_CURRENCY, currency).apply()
        getSharedPreferences()
    }

    private fun editDateFormatSP(){
        val sharedDateFormat = requireContext().getSharedPreferences(SHARED_PREFERENCES_DATE_FORMAT, Context.MODE_PRIVATE)
        sharedDateFormat.edit().putString(KEY_CURRENT_DATE_FORMAT, dateFormat).apply()
    }

    private fun getSharedPreferences() {
        val sharedCurrency = requireContext().getSharedPreferences(SHARED_PREFERENCES_CURRENCY, Context.MODE_PRIVATE)
        listCurrencies = UtilsCurrency.getListCurrenciesFromSharedPreferences(sharedCurrency)
        val currentString = sharedCurrency.getString(KEY_CURRENT_CURRENCY, null)
        if(currentString != null) {
            currentCurrency = UtilsCurrency.castStringInCurrency(sharedCurrency.getString(KEY_CURRENT_CURRENCY, null)!!)
        } else {
            currentCurrency = listCurrencies[0]
        }

        val sharedDateFormat = requireContext().getSharedPreferences(SHARED_PREFERENCES_DATE_FORMAT, Context.MODE_PRIVATE)
        dateFormat = sharedDateFormat.getString(KEY_CURRENT_DATE_FORMAT, null)
    }

    private fun populateBtnWithSP() {
        when(currentCurrency.code) {
            CURRENCY_USD.toUpperCase() -> mView.btn_usd.isChecked = true
            CURRENCY_CAD.toUpperCase() -> mView.btn_cad.isChecked = true
            CURRENCY_GBP.toUpperCase()-> mView.btn_gbp.isChecked = true
            CURRENCY_EUR.toUpperCase() -> mView.btn_eur.isChecked = true
            CURRENCY_JPY.toUpperCase() -> mView.btn_jpy.isChecked = true
            CURRENCY_AUD.toUpperCase() -> mView.btn_aud.isChecked = true
            else -> mView.btn_usd.isChecked = true
        }

        when(dateFormat) {
            null -> mView.btn_date_us.isChecked = true
            US_DATE_FORMAT -> mView.btn_date_us.isChecked = true
            FR_DATE_FORMAT -> mView.btn_date_fr.isChecked = true
        }

    }

}