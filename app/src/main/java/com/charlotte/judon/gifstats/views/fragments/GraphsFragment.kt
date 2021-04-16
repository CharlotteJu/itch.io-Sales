package com.charlotte.judon.gifstats.views.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.charts.Cartesian
import com.anychart.data.Set
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.MpBarReturn
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.Utils
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_graphs.view.*


class GraphsFragment : Fragment() {
    private lateinit var salesList : List<Sale>
    private lateinit var mView: View
    private lateinit var salesListFiltered : List<Sale>
    private var btnChecked = 0
    private lateinit var chartVertical : Cartesian
    private lateinit var set : Set
    private var btnPackageChecked = 0

    companion object {

        @JvmStatic
        fun newInstance(salesList: List<Sale>) : GraphsFragment
        {
            return GraphsFragment()
                .apply {
                this.salesList = salesList
                this.salesListFiltered = salesList
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        mView = inflater.inflate(R.layout.fragment_graphs, container, false)

        configureGraphView()
        configureSpinner()
        initPackageGraph()

        mView.radio_group_choice.setOnCheckedChangeListener { _, checkedId ->
            btnChecked = checkedId
            when (checkedId) {

                R.id.btn_hour -> getBarGraphByHour()
                R.id.btn_day_of_week -> getBarGraphByDayOfWeek()
                R.id.btn_date -> getBarGraphByDate()
                R.id.btn_package -> getBarGraphByPackage()
            }

        }
        mView.btn_hour.isChecked = true
        return mView
    }

    private fun configureSpinner()
    {
        val spinnerFilter = resources.getStringArray(R.array.spinner_filter)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, spinnerFilter)
        mView.spinner.adapter = adapter
        mView.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                when (position) {
                    0 -> {
                        salesListFiltered = salesList
                        chartVertical.data(getListForPackage())
                        checkBtn()
                    }
                    1 -> {
                        val dateStart = Utils.getDateStartToFilter(7)
                        salesListFiltered = Utils.filterList(salesList, dateStart)
                        chartVertical.data(getListForPackage())
                        checkBtn()
                    }
                    2 -> {
                        val dateStart = Utils.getDateStartToFilter(30)
                        salesListFiltered = Utils.filterList(salesList, dateStart)
                        chartVertical.data(getListForPackage())
                        checkBtn()
                    }
                }
            }
        }
    }

    private fun checkBtn()
    {
        val temp = btnChecked
        if(btnChecked == R.id.btn_hour) {
            mView.btn_date.isChecked = true
        } else {
            mView.btn_hour.isChecked = true
        }
        when (temp) {
            R.id.btn_hour -> mView.btn_hour.isChecked = true
            R.id.btn_day_of_week -> mView.btn_day_of_week.isChecked = true
            R.id.btn_date -> mView.btn_date.isChecked = true
            R.id.btn_package -> mView.btn_package.isChecked = true
        }

    }

    private fun getListForPackage() : List<DataEntry>
    {
        return if (btnPackageChecked == R.id.btn_nb) {
            Utils.anyChartPackageNB(this.salesListFiltered)
        } else {
            Utils.anyChartPackagePrice(this.salesListFiltered)
        }
    }

    private fun getBarGraphByHour()
    {
        mView.MpBarView.visibility = View.VISIBLE
        mView.anyChartView.visibility = View.GONE
        mView.radio_group_package.visibility = View.GONE
        mView.MpBarView.invalidate()
        val mpBarReturn = Utils.graphMPBarByHour(salesListFiltered)
        if(mpBarReturn.barDataSet == null) {
            mView.MpBarView.visibility = View.GONE
            mView.no_sales_graph.visibility = View.VISIBLE
        } else {
            mView.MpBarView.visibility = View.VISIBLE
            mView.no_sales_graph.visibility = View.GONE

            val data = BarData(mpBarReturn.barDataSet)
            val formatterX = null

            mView.MpBarView.data = data
            mView.MpBarView.xAxis.valueFormatter = formatterX

        }
    }

    private fun getBarGraphByDayOfWeek()
    {
        mView.MpBarView.visibility = View.VISIBLE
        mView.anyChartView.visibility = View.GONE
        mView.radio_group_package.visibility = View.GONE
        mView.MpBarView.invalidate()
        val mpBarReturn = Utils.graphMPBarByDayOfWeek(salesListFiltered)
        if(mpBarReturn.barDataSet == null) {
            mView.MpBarView.visibility = View.GONE
            mView.no_sales_graph.visibility = View.VISIBLE
        } else {
            mView.MpBarView.visibility = View.VISIBLE
            mView.no_sales_graph.visibility = View.GONE

            val data = BarData(mpBarReturn.barDataSet)
            val formatterX = IndexAxisValueFormatter(mpBarReturn.listString)

            mView.MpBarView.data = data
            mView.MpBarView.xAxis.valueFormatter = formatterX

        }
    }



    private fun getBarGraphByDate()
    {
        //mView.MpBarView.visibility = View.VISIBLE
        mView.anyChartView.visibility = View.GONE
        mView.radio_group_package.visibility = View.GONE
        mView.MpBarView.invalidate()
        val mpBarReturn = Utils.graphMPByDay(salesListFiltered)

        if(mpBarReturn.barDataSet == null) {
            mView.MpBarView.visibility = View.GONE
            mView.no_sales_graph.visibility = View.VISIBLE
        } else {
            mView.MpBarView.visibility = View.VISIBLE
            mView.no_sales_graph.visibility = View.GONE

            val barDataSet = mpBarReturn.barDataSet
            barDataSet.color = Color.parseColor("#F80039")
            barDataSet.highLightColor = Color.parseColor("#4B2E5A")
            barDataSet.setDrawValues(false)

            val data = BarData(barDataSet)
            val formatterX = IndexAxisValueFormatter(mpBarReturn.listString)

            mView.MpBarView.data = data
            mView.MpBarView.xAxis.valueFormatter = formatterX

        }
    }

    private fun getBarGraphByPackage()
    {
        mView.MpBarView.visibility = View.GONE
        mView.MpBarView.invalidate()
        if(salesListFiltered.isEmpty()) {
            mView.no_sales_graph.visibility = View.VISIBLE
            mView.anyChartView.visibility = View.GONE
            mView.radio_group_package.visibility = View.GONE
        } else {
            mView.no_sales_graph.visibility = View.GONE
            mView.anyChartView.visibility = View.VISIBLE
            mView.radio_group_package.visibility = View.VISIBLE
        }

        APIlib.getInstance().setActiveAnyChartView(mView.anyChartView)
    }

    private fun configureGraphView()
    {
        mView.MpBarView.description.isEnabled = false
        mView.MpBarView.legend.isEnabled = false
        mView.MpBarView.xAxis.position = XAxis.XAxisPosition.BOTTOM
        mView.MpBarView.axisLeft.axisMinimum = 0f
        mView.MpBarView.axisRight.axisMinimum = 0f

        //mView.MpBarView.marker = MarkerView(requireContext(), R.layout.test_marker)
    }

    private fun initPackageGraph()
    {
        APIlib.getInstance().setActiveAnyChartView(mView.anyChartView)
        chartVertical = AnyChart.vertical()

        chartVertical.legend().title().enabled(true)
        chartVertical.animation(true)
        val packageList = Utils.anyChartPackageNB(salesListFiltered)
        val column = chartVertical.bar(packageList)
        column.fill("function() {" +
                "            return '#F80039';" +
                "        }")
        column.stroke("function() {" +
                "            return '#F80039';" +
                "        }")

        mView.anyChartView.setChart(chartVertical)

        mView.radio_group_package.setOnCheckedChangeListener { _, checkedId ->
            btnPackageChecked = checkedId
            when(checkedId) {
                R.id.btn_nb -> chartVertical.data(Utils.anyChartPackageNB(this.salesListFiltered))
                R.id.btn_price -> chartVertical.data(Utils.anyChartPackagePrice(this.salesListFiltered))
            }
        }
        mView.btn_nb.isChecked = true
    }

}



