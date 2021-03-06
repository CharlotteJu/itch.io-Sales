package com.charlotte.judon.gifstats.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.DateDetail
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.UtilsGeneral
import com.charlotte.judon.gifstats.utils.UtilsCharts
import com.charlotte.judon.gifstats.views.adapters.DateDetailAdapter
import kotlinx.android.synthetic.main.fragment_date_detail.view.*
import kotlinx.android.synthetic.main.fragment_date_detail.view.radio_group_currency
import kotlinx.android.synthetic.main.fragment_date_detail.view.spinner

/**
 * Fragment used to show number's [Sale] by day of week with hours
 * @link [MPChartAndroid : https://github.com/PhilJay/MPAndroidChart]
 * @author Charlotte JUDON
 */
class DateDetailFragment : Fragment() {

    private lateinit var salesList: List<Sale>
    private lateinit var salesListFiltered : List<Sale>
    private lateinit var mView: View
    private lateinit var adapterAll: DateDetailAdapter
    private var dateDetailList = mutableListOf<DateDetail>()
    private var listOf3Best = mutableListOf<DateDetail>()
    private var is3Best = false

    companion object {
        @JvmStatic
        fun newInstance(list: List<Sale>) =
            DateDetailFragment().apply {
                this.salesList = list
                this.salesListFiltered = list
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_date_detail, container, false)

        if(!this::salesList.isInitialized) UtilsGeneral.goBackToHomeFragment(this)
        else init()

        return mView
    }

    private fun init(){
        getDateDetailList()
        configureRcv()
        configureSpinner()

        mView.radio_group_currency.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btn_all -> {
                    is3Best = false
                    updateList()
                }
                R.id.btn_3_best -> {
                    is3Best = true
                    updateList()
                }
            }
        }
        mView.btn_all.isChecked = true
    }

    private fun configureRcv(){
        adapterAll = DateDetailAdapter(dateDetailList, requireContext())
        mView.graph_rcv_all.adapter = adapterAll
        mView.graph_rcv_all.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun configureSpinner() {
        val spinnerFilter = resources.getStringArray(R.array.spinner_filter)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, spinnerFilter)
        mView.spinner.adapter = adapter

        mView.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                when (position) {
                    0 -> {
                        salesListFiltered = salesList
                        updateList()
                    }
                    1 -> {
                        val dateStart = UtilsGeneral.getDateStartToFilter(7)
                        salesListFiltered = UtilsGeneral.filterList(salesList, dateStart)
                        updateList()
                    }
                    2 -> {
                        val dateStart = UtilsGeneral.getDateStartToFilter(30)
                        salesListFiltered = UtilsGeneral.filterList(salesList, dateStart)
                        updateList()
                    }
                }
            }
        }
    }

    private fun updateList() {
        getDateDetailList()
        if(is3Best) {
            adapterAll.notifyDataChanged(listOf3Best)
        } else {
            adapterAll.notifyDataChanged(dateDetailList)
        }
        updateViews()
    }

    /**
     * Show or Hide views according to [dateDetailList]
     */
    private fun updateViews()  {
        var nbSales = 0

        for (day in dateDetailList) {
            nbSales += day.totalSale
        }
        if (nbSales == 0) {
            mView.no_sales_date_detail.visibility = View.VISIBLE
            mView.graph_rcv_all.visibility = View.INVISIBLE
        } else {
            mView.graph_rcv_all.visibility = View.VISIBLE
            mView.no_sales_date_detail.visibility = View.INVISIBLE
        }
    }

    /**
     * Populate the [dateDetailList] for each day of week
     */
    private fun getDateDetailList() {
        dateDetailList.clear()
        dateDetailList.add(UtilsCharts.getMPChartByDayWithHours(salesListFiltered, "Mon", requireContext()))
        dateDetailList.add(UtilsCharts.getMPChartByDayWithHours(salesListFiltered, "Tue", requireContext()))
        dateDetailList.add(UtilsCharts.getMPChartByDayWithHours(salesListFiltered, "Wed", requireContext()))
        dateDetailList.add(UtilsCharts.getMPChartByDayWithHours(salesListFiltered, "Thu", requireContext()))
        dateDetailList.add(UtilsCharts.getMPChartByDayWithHours(salesListFiltered, "Fri", requireContext()))
        dateDetailList.add(UtilsCharts.getMPChartByDayWithHours(salesListFiltered, "Sat", requireContext()))
        dateDetailList.add(UtilsCharts.getMPChartByDayWithHours(salesListFiltered, "Sun", requireContext()))
        getList3Best()
    }

    /**
     *
     */
    private fun getList3Best() {
        val listTemp = dateDetailList.sortedWith(compareBy { it.totalSale }).reversed()
        listOf3Best.clear()
        listOf3Best.add(listTemp[0])
        listOf3Best.add(listTemp[1])
        listOf3Best.add(listTemp[2])
    }

}