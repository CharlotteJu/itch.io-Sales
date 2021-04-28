package com.charlotte.judon.gifstats.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.CustomCurrency
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.views.adapters.SaleAdapter
import kotlinx.android.synthetic.main.fragment_list_sales.view.*

class ListSalesFragment : Fragment() {
    private lateinit var salesList : List<Sale>
    private lateinit var adapter : SaleAdapter
    private lateinit var mView: View
    private lateinit var listCurrencies : List<CustomCurrency>
    private lateinit var currentCurrency : CustomCurrency
    private lateinit var dateFormat : String

    companion object {

        @JvmStatic
        fun newInstance(salesList: List<Sale>, currentCurrency: CustomCurrency, listCurrencies : List<CustomCurrency>, dateFormat : String) : ListSalesFragment
        {
            return ListSalesFragment().apply {
                this.salesList = salesList
                this.currentCurrency = currentCurrency
                this.listCurrencies = listCurrencies
                this.dateFormat = dateFormat
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        mView = inflater.inflate(R.layout.fragment_list_sales, container, false)
        configureRcv()
        return mView
    }

    private fun configureRcv()
    {
        adapter = SaleAdapter(salesList, currentCurrency, listCurrencies, dateFormat)
        mView.rcv.adapter = adapter
        mView.rcv.layoutManager = LinearLayoutManager(requireContext())

    }

}