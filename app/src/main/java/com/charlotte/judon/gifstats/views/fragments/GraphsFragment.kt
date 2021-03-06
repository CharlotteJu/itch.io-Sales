package com.charlotte.judon.gifstats.views.fragments

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
import com.charlotte.judon.gifstats.BuildConfig
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.CustomCurrency
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.UtilsCharts
import com.charlotte.judon.gifstats.utils.UtilsGeneral
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_graphs.view.*
import kotlinx.android.synthetic.main.fragment_graphs.view.spinner

/**
 * Fragment used to show MPChartAndroid (https://github.com/PhilJay/MPAndroidChart) on :
 * - Number's [Sale] by hour of day
 * - Number's [Sale] by day of week
 * - Number's [Sale] by date
 * Fragment used to show AnyChartAndroid (https://github.com/AnyChart/AnyChart-Android) on :
 * - Number's [Sale] by package
 * - Price's [Sale] by package
 * @author Charlotte JUDON
 */
class GraphsFragment : Fragment(), OnChartValueSelectedListener {
    private lateinit var salesList : List<Sale>
    private lateinit var mView: View
    private lateinit var salesListFiltered : List<Sale>
    private var btnChecked = 0
    private lateinit var chartVertical : Cartesian
    private lateinit var set : Set
    private var xFormatter: IndexAxisValueFormatter? = null
    private var btnPackageChecked = 0
    private lateinit var listCurrencies : List<CustomCurrency>
    private lateinit var currentCurrency : CustomCurrency
    private lateinit var dateFormat : String


    companion object {

        @JvmStatic
        fun newInstance(salesList: List<Sale>, currentCurrency: CustomCurrency,
                        listCurrencies : List<CustomCurrency>, dateFormat : String) : GraphsFragment
        {
            return GraphsFragment()
                .apply {
                    this.salesList = salesList
                    this.salesListFiltered = salesList
                    this.currentCurrency = currentCurrency
                    this.listCurrencies = listCurrencies
                    this.dateFormat = dateFormat
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_graphs, container, false)

        if(!this::currentCurrency.isInitialized) UtilsGeneral.goBackToHomeFragment(this)
        else init()

        return mView
    }

    private fun init(){

        configureGraphView()
        configureSpinner()
        initPackageGraph()

        mView.radio_group_currency.setOnCheckedChangeListener { _, checkedId ->
            btnChecked = checkedId
            when (checkedId) {

                R.id.btn_hour -> getBarGraphByHour()
                R.id.btn_day_of_week -> getBarGraphByDayOfWeek()
                R.id.btn_date -> getBarGraphByDate()
                R.id.btn_package -> getBarGraphByPackage()
            }

        }
        mView.btn_hour.isChecked = true
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
                        chartVertical.data(getListForPackage())
                        checkBtn()
                    }
                    1 -> {
                        val dateStart = UtilsGeneral.getDateStartToFilter(7)
                        salesListFiltered = UtilsGeneral.filterList(salesList, dateStart)
                        chartVertical.data(getListForPackage())
                        checkBtn()
                    }
                    2 -> {
                        val dateStart = UtilsGeneral.getDateStartToFilter(30)
                        salesListFiltered = UtilsGeneral.filterList(salesList, dateStart)
                        chartVertical.data(getListForPackage())
                        checkBtn()
                    }
                }
            }
        }
    }

    private fun checkBtn() {
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

    private fun getListForPackage() : List<DataEntry> {
        return if (btnPackageChecked == R.id.btn_nb) {
            UtilsCharts.getAnyChartPackageNumber(this.salesListFiltered)
        } else {
            UtilsCharts.getAnyChartPackagePrice(this.salesListFiltered, currentCurrency, listCurrencies)
        }
    }

    private fun getBarGraphByHour() {
        onNothingSelected()
        mView.MpBarView.visibility = View.VISIBLE
        mView.anyChartView.visibility = View.GONE
        mView.radio_group_package.visibility = View.GONE
        mView.MpBarView.invalidate()
        val mpBarReturn = UtilsCharts.getMPChartByHour(salesListFiltered)
        if(mpBarReturn.barDataSet == null) {
            mView.MpBarView.visibility = View.GONE
            mView.no_sales_graph.visibility = View.VISIBLE
        } else {
            mView.MpBarView.visibility = View.VISIBLE
            mView.no_sales_graph.visibility = View.GONE

            val data = BarData(mpBarReturn.barDataSet)
            xFormatter = null

            mView.MpBarView.data = data
            mView.MpBarView.xAxis.valueFormatter = xFormatter

        }
    }

    private fun getBarGraphByDayOfWeek() {
        onNothingSelected()
        mView.MpBarView.visibility = View.VISIBLE
        mView.anyChartView.visibility = View.GONE
        mView.radio_group_package.visibility = View.GONE
        mView.MpBarView.invalidate()
        val mpBarReturn = UtilsCharts.getMPChartByDayOfWeek(salesListFiltered)
        if(mpBarReturn.barDataSet == null) {
            mView.MpBarView.visibility = View.GONE
            mView.no_sales_graph.visibility = View.VISIBLE
        } else {
            mView.MpBarView.visibility = View.VISIBLE
            mView.no_sales_graph.visibility = View.GONE

            val data = BarData(mpBarReturn.barDataSet)
            xFormatter = IndexAxisValueFormatter(mpBarReturn.listString)

            mView.MpBarView.data = data
            mView.MpBarView.xAxis.valueFormatter = xFormatter

        }
    }

    private fun getBarGraphByDate() {
        onNothingSelected()
        mView.anyChartView.visibility = View.GONE
        mView.radio_group_package.visibility = View.GONE
        mView.MpBarView.invalidate()
        val mpBarReturn = UtilsCharts.getMPChartByDay(salesListFiltered, dateFormat)

        if(mpBarReturn.barDataSet == null) {
            mView.MpBarView.visibility = View.GONE
            mView.no_sales_graph.visibility = View.VISIBLE
            mView.marker_text_all_graph.visibility = View.GONE
        } else {
            mView.MpBarView.visibility = View.VISIBLE
            mView.no_sales_graph.visibility = View.GONE
            mView.marker_text_all_graph.visibility = View.VISIBLE

            val data = BarData(mpBarReturn.barDataSet)
            xFormatter = IndexAxisValueFormatter(mpBarReturn.listString)
            mView.MpBarView.data = data
            mView.MpBarView.xAxis.valueFormatter = xFormatter

        }
    }

    private fun getBarGraphByPackage() {
        onNothingSelected()
        mView.MpBarView.visibility = View.GONE
        mView.MpBarView.invalidate()
        if(salesListFiltered.isEmpty()) {
            mView.no_sales_graph.visibility = View.VISIBLE
            mView.anyChartView.visibility = View.GONE
            mView.radio_group_package.visibility = View.GONE
            mView.marker_text_all_graph.visibility = View.GONE
        } else {
            mView.no_sales_graph.visibility = View.GONE
            mView.anyChartView.visibility = View.VISIBLE
            mView.radio_group_package.visibility = View.VISIBLE
            mView.marker_text_all_graph.visibility = View.GONE
        }

        APIlib.getInstance().setActiveAnyChartView(mView.anyChartView)
    }

    private fun configureGraphView() {
        mView.MpBarView.description.isEnabled = false
        mView.MpBarView.legend.isEnabled = false
        mView.MpBarView.xAxis.position = XAxis.XAxisPosition.BOTTOM
        mView.MpBarView.axisLeft.axisMinimum = 0f
        mView.MpBarView.axisRight.axisMinimum = 0f
        mView.MpBarView.setOnChartValueSelectedListener(this)
        mView.MpBarView.xAxis.valueFormatter = xFormatter
    }

    /**
     *
     */
    private fun initPackageGraph() {
        APIlib.getInstance().setActiveAnyChartView(mView.anyChartView)
        chartVertical = AnyChart.vertical()
        chartVertical.legend().title().enabled(true)
        chartVertical.animation(true)
        val packageList = UtilsCharts.getAnyChartPackageNumber(salesListFiltered)
        val column = chartVertical.bar(packageList)
        column.name(resources.getString(R.string.Sales))
        column.fill("function() {" +
                "            return '#FA5C5C';" +
                "        }")
        column.stroke("function() {" +
                "            return '#FA5C5C';" +
                "        }")

        mView.anyChartView.setChart(chartVertical)
        mView.anyChartView.setLicenceKey(BuildConfig.ANYCHART_LICENCE_KEY)

        mView.radio_group_package.setOnCheckedChangeListener { _, checkedId ->
            btnPackageChecked = checkedId
            when(checkedId) {
                R.id.btn_nb -> chartVertical.data(UtilsCharts.getAnyChartPackageNumber(this.salesListFiltered))
                R.id.btn_price -> chartVertical.data(UtilsCharts.getAnyChartPackagePrice(this.salesListFiltered, currentCurrency, listCurrencies))
            }
        }
        mView.btn_nb.isChecked = true
    }

    /**
     * Display the value of BarChart in TextView
     */
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e != null) {
            val saleString = if (e.y > 1) {
                resources.getString(R.string.sales)
            } else {
                resources.getString(R.string.sale)
            }
            if(xFormatter == null) {
                mView.marker_text_all_graph.text = "${e.x.toInt()} h : ${e.y.toInt()} $saleString"
            } else {
                mView.marker_text_all_graph.text = "${xFormatter!!.getFormattedValue(e.x)} : ${e.y.toInt()}  $saleString"
            }
        }
    }

    override fun onNothingSelected() {
        mView.marker_text_all_graph.text = ""
    }

}



