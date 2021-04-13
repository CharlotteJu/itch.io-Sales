package com.charlotte.judon.gifstats.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.DateDetail
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.Utils
import com.charlotte.judon.gifstats.views.adapters.DateDetailAdapter
import kotlinx.android.synthetic.main.fragment_date_detail.*
import kotlinx.android.synthetic.main.fragment_date_detail.view.*
import kotlinx.android.synthetic.main.fragment_date_detail.view.radio_group
import kotlinx.android.synthetic.main.fragment_date_detail.view.spinner


class DateDetailFragment : Fragment() {

    private lateinit var salesList: List<Sale>
    private lateinit var salesListFiltered : List<Sale>
    private lateinit var mView: View
    private lateinit var adapterAll: DateDetailAdapter
    private lateinit var adapter3Best : DateDetailAdapter
    private val dateDetailList = mutableListOf<DateDetail>()
    private val listOf3Best = mutableListOf<DateDetail>()

    companion object {
        @JvmStatic
        fun newInstance(list: List<Sale>) =
            DateDetailFragment().apply {
                this.salesList = list
                this.salesListFiltered = list
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_date_detail, container, false)
        getDateDetailList()
        configureRcv()
        configureSpinner()

        mView.radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btn_all -> getAll()
                R.id.btn_3_best -> get3Best()
            }
        }
        mView.btn_3_best.isChecked = true

        return mView
    }

    private fun configureRcv(){
        adapterAll =
            DateDetailAdapter(
                dateDetailList
            )
        adapter3Best =
            DateDetailAdapter(
                listOf3Best
            )
        mView.graph_rcv_all.adapter = adapterAll
        mView.graph_rcv_3_best.adapter = adapter3Best
        mView.graph_rcv_all.layoutManager = LinearLayoutManager(requireContext())
        mView.graph_rcv_3_best.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun configureSpinner() {
        val spinnerFilter = resources.getStringArray(R.array.spinner_filter)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, spinnerFilter)
        mView.spinner.adapter = adapter

        mView.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                when (position) {
                    0 -> { salesListFiltered = salesList
                        updateList()
                    }
                    1 -> { val dateStart = Utils.getDateStartToFilter(7)
                        salesListFiltered = Utils.filterList(salesList, dateStart)
                        updateList()
                    }
                    2 -> { val dateStart = Utils.getDateStartToFilter(30)
                        salesListFiltered = Utils.filterList(salesList, dateStart)
                        updateList()
                    }
                }
            }
        }
    }

    private fun updateList() {
        getDateDetailList()
        adapterAll.notifyDataChanged(dateDetailList)
        adapter3Best.notifyDataChanged(listOf3Best)
        updateViews()
    }

    private fun get3Best() {
        mView.graph_rcv_all.visibility = View.INVISIBLE
        mView.graph_rcv_3_best.visibility = View.VISIBLE
    }

    private fun updateViews()  {
        var nbSales = 0

        for (day in dateDetailList) {
            nbSales += day.totalSale
        }
        if (nbSales == 0) {
            val textToast = resources.getText(R.string.no_sales_period)
            Toast.makeText(this.requireContext(), textToast, Toast.LENGTH_LONG).show()
            mView.graph_rcv_all.visibility = View.INVISIBLE
            mView.graph_rcv_3_best.visibility = View.INVISIBLE
        } else {
            if (btn_3_best.isChecked) get3Best()
            else getAll()
        }
    }

    private fun getAll() {
        mView.graph_rcv_all.visibility = View.VISIBLE
        mView.graph_rcv_3_best.visibility = View.INVISIBLE
    }

    private fun getDateDetailList() {
        dateDetailList.clear()
        dateDetailList.add(Utils.graphMPBarByDayWithHours(salesListFiltered, "Mon", requireContext()))
        dateDetailList.add(Utils.graphMPBarByDayWithHours(salesListFiltered, "Tue", requireContext()))
        dateDetailList.add(Utils.graphMPBarByDayWithHours(salesListFiltered, "Wed", requireContext()))
        dateDetailList.add(Utils.graphMPBarByDayWithHours(salesListFiltered, "Thu", requireContext()))
        dateDetailList.add(Utils.graphMPBarByDayWithHours(salesListFiltered, "Fri", requireContext()))
        dateDetailList.add(Utils.graphMPBarByDayWithHours(salesListFiltered, "Sat", requireContext()))
        dateDetailList.add(Utils.graphMPBarByDayWithHours(salesListFiltered, "Sun", requireContext()))
        getList3Best()
    }

    private fun getList3Best() {
        val listTemp = dateDetailList.sortedWith(compareBy { it.totalSale }).reversed()
        listOf3Best.clear()
        listOf3Best.add(listTemp[0])
        listOf3Best.add(listTemp[1])
        listOf3Best.add(listTemp[2])
    }


}