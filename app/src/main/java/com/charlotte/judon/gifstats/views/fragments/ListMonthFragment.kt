package com.charlotte.judon.gifstats.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.CustomCurrency
import com.charlotte.judon.gifstats.model.MonthSale
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.*
import com.charlotte.judon.gifstats.views.adapters.SaleMonthAdapter
import kotlinx.android.synthetic.main.fragment_list_month.view.*

/**
 * Fragment used to show total [Sale]'s information by month
 * @link [MonthSale]
 * @author Charlotte JUDON
 */
class ListMonthFragment : Fragment(),
    SaleMonthAdapter.OnClickMonthItem {
    private lateinit var salesList : List<Sale>
    private lateinit var adapter : SaleMonthAdapter
    private lateinit var mView: View
    private lateinit var saleMonthList : List<MonthSale>
    private lateinit var listCurrencies : List<CustomCurrency>
    private lateinit var currentCurrency : CustomCurrency
    private lateinit var dateFormat : String

    companion object {

        @JvmStatic
        fun newInstance(salesList: List<Sale>, currentCurrency: CustomCurrency,
                        listCurrencies : List<CustomCurrency>, dateFormat : String) : ListMonthFragment
        {
            return ListMonthFragment()
                .apply {
                    this.salesList = salesList
                    this.currentCurrency = currentCurrency
                    this.listCurrencies = listCurrencies
                    this.dateFormat = dateFormat
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        mView = inflater.inflate(R.layout.fragment_list_month, container, false)
        if(!this::currentCurrency.isInitialized) {
            UtilsGeneral.goBackToHomeFragment(this)
        }
        else {
            configurePrices()
        }
        return mView
    }

    private fun configureRcv()
    {
        adapter = SaleMonthAdapter(saleMonthList, this, requireContext(), currentCurrency.symbol)
        mView.rcv_month.adapter = adapter
        mView.rcv_month.layoutManager = LinearLayoutManager(requireContext())

    }

    /**
     * Change text in Views total Price
     */
    private fun configurePrices() {
        saleMonthList = UtilsGeneral.castSaleListInSaleMonthList(salesList, currentCurrency, listCurrencies)

        if(saleMonthList.isNotEmpty()) {
            changeViews(true)
            val totalBrut = UtilsGeneral.calculationTotalGrossSales(salesList, currentCurrency, listCurrencies)
            mView.total_brut_txt.text = "$totalBrut ${currentCurrency.symbol}"

            val totalNet = UtilsGeneral.calculationTotalNetSales(salesList, currentCurrency, listCurrencies)
            mView.total_net_txt.text = "$totalNet ${currentCurrency.symbol}"

            val charges = UtilsGeneral.calculChargesSales(totalBrut, totalNet)
            mView.charges_txt.text = "$charges %"

            configureRcv()
        } else {
            changeViews(false)
        }
    }

    private fun changeViews(bool : Boolean){
        if (bool) {
            mView.container_total.visibility = View.VISIBLE
            mView.container_title.visibility = View.VISIBLE
            mView.rcv_month.visibility = View.VISIBLE
            mView.no_sales_month.visibility = View.INVISIBLE
        } else {
            mView.container_total.visibility = View.INVISIBLE
            mView.container_title.visibility = View.INVISIBLE
            mView.rcv_month.visibility = View.INVISIBLE
            mView.no_sales_month.visibility = View.VISIBLE
        }
    }

    override fun onClickMonthItem(saleList: List<Sale>) {
        parentFragmentManager.apply {
            val listFragment = ListSalesFragment.newInstance(saleList, currentCurrency, listCurrencies, dateFormat)
            val ft = this.beginTransaction()
            ft.replace(R.id.container, listFragment).addToBackStack(BACKSTACK).commit()
        }
    }
}