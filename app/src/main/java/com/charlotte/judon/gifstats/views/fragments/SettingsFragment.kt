package com.charlotte.judon.gifstats.views.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.CustomCurrency
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.*
import com.charlotte.judon.gifstats.viewModel.ViewModel
import com.charlotte.judon.gifstats.views.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    private lateinit var viewModel : ViewModel
    private lateinit var listSales : List<Sale>
    private lateinit var mView : View
    private lateinit var currency : String
    private lateinit var currentCurrency : CustomCurrency
    private lateinit var dateFormat : String
    private lateinit var listCurrencies : List<CustomCurrency>

    companion object {

        @JvmStatic
        fun newInstance(viewModel: ViewModel, listSales : List<Sale>, currentCurrency: CustomCurrency,
                        listCurrencies : List<CustomCurrency>, dateFormat : String) : SettingsFragment
        {
            return SettingsFragment()
                .apply {
                    this.viewModel = viewModel
                    this.listSales = listSales
                    this.currentCurrency = currentCurrency
                    this.listCurrencies = listCurrencies
                    this.dateFormat = dateFormat
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

        populateBtnWithSP()

        mView.radio_group_currency.setOnCheckedChangeListener { _, checkedId ->
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

        mView.radio_group_date_format.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btn_date_us -> dateFormat = US_DATE_FORMAT
                R.id.btn_date_fr -> dateFormat = FR_DATE_FORMAT
            }
            editDateFormatSP()
        }

        mView.button_instructions.setOnClickListener {
            goToInstructions()
        }

        mView.button_delete.setOnClickListener {
            getAlertDialog()
        }

        return mView
    }

    private fun editCurrencySP(){
        val sharedCurrency = requireContext().getSharedPreferences(SHARED_PREFERENCES_CURRENCY, Context.MODE_PRIVATE)
        sharedCurrency.edit().putString(KEY_CURRENT_CURRENCY, currency).apply()
        (activity as MainActivity).getSharedPreferences()
    }

    private fun editDateFormatSP(){
        val sharedDateFormat = requireContext().getSharedPreferences(SHARED_PREFERENCES_DATE_FORMAT, Context.MODE_PRIVATE)
        sharedDateFormat.edit().putString(KEY_CURRENT_DATE_FORMAT, dateFormat).apply()
        (activity as MainActivity).getSharedPreferences()
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
            US_DATE_FORMAT -> mView.btn_date_us.isChecked = true
            FR_DATE_FORMAT -> mView.btn_date_fr.isChecked = true
        }

    }

    private fun getAlertDialog(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
        builder.setMessage(R.string.dialog_message)
        builder.setPositiveButton(R.string.dialog_yes) { _, _ ->
            viewModel.deleteAllSales(requireContext(), listSales)
        }
        builder.setNegativeButton(R.string.dialog_no) { _, _ ->}
        builder.create()
        builder.show()
    }

    private fun goToInstructions() {
        parentFragmentManager.apply {
            val instructionsFragment = InstructionsFragment()
            val ft = this.beginTransaction()
            ft.replace(R.id.container, instructionsFragment).addToBackStack(BACKSTACK).commit()
        }
    }

}