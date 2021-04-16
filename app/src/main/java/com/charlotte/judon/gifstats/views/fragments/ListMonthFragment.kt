package com.charlotte.judon.gifstats.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlotte.judon.gifstats.views.activities.BACKSTACK
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.MonthSale
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.Utils
import com.charlotte.judon.gifstats.views.adapters.SaleMonthAdapter
import kotlinx.android.synthetic.main.fragment_list.view.rcv
import kotlinx.android.synthetic.main.fragment_list_month.view.*

class ListMonthFragment : Fragment(),
    SaleMonthAdapter.OnClickMonthItem {
    private lateinit var salesList : List<Sale>
    private lateinit var adapter : SaleMonthAdapter
    private lateinit var mView: View
    private lateinit var saleMonthList : List<MonthSale>

    companion object {

        @JvmStatic
        fun newInstance(salesList: List<Sale>) : ListMonthFragment
        {
            return ListMonthFragment()
                .apply {
                this.salesList = salesList
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        mView = inflater.inflate(R.layout.fragment_list_month, container, false)
        configurePrices()
        configureRcv()
        return mView
    }

    private fun configureRcv()
    {
        adapter = SaleMonthAdapter(
            saleMonthList,
            this,
            requireContext()
        )
        mView.rcv.adapter = adapter
        mView.rcv.layoutManager = LinearLayoutManager(requireContext())

    }


    private fun configurePrices() {
        val totalBrut = Utils.calculTotalBrutSales(salesList)
        mView.total_brut_txt.text = "$totalBrut €"

        val totalNet = Utils.calculTotalNetSales(salesList)
        mView.total_net_txt.text = "$totalNet €"

        val charges = Utils.calculChargesSales(totalBrut, totalNet)
        mView.charges_txt.text = "$charges %"

        saleMonthList = Utils.castSaleListInSaleMonthList(salesList)
    }

    override fun onClickMonthItem(saleList: List<Sale>) {
        parentFragmentManager.apply {
            val listFragment = ListFragment.newInstance(saleList)
            val ft = this.beginTransaction()
            ft.replace(R.id.container, listFragment).addToBackStack(BACKSTACK).commit()
        }
    }
}