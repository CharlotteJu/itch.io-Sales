package com.charlotte.judon.gifstats.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.core.map.series.Choropleth
import com.anychart.core.ui.ColorRange
import com.anychart.enums.*
import com.anychart.graphics.vector.SolidFill
import com.anychart.scales.OrdinalColor
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.ChronopletReturn
import com.charlotte.judon.gifstats.model.Country
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.UtilsGeneral
import com.charlotte.judon.gifstats.utils.UtilsCharts
import com.charlotte.judon.gifstats.views.adapters.CountryAdapter
import kotlinx.android.synthetic.main.fragment_map.view.*


class MapFragment : Fragment() {
    private lateinit var salesList : List<Sale>
    private lateinit var mView: View
    private lateinit var salesListFiltered : List<Sale>
    private lateinit var serieChronoplet : Choropleth
    private lateinit var ordinalColor: OrdinalColor
    private lateinit var adapter : CountryAdapter

    companion object {

        @JvmStatic
        fun newInstance(salesList: List<Sale>) : MapFragment
        {
            return MapFragment()
                .apply {
                this.salesList = salesList
                this.salesListFiltered = salesList
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_map, container, false)

        configureSpinner()
        initChronopleth()

        return mView
    }

    private fun configureSpinner() {
        val spinnerFilter = resources.getStringArray(R.array.spinner_filter)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, spinnerFilter)
        mView.spinner.adapter = adapter

        mView.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                when (position) {
                    0 -> getFunSpinner(null)
                    1 -> getFunSpinner(7)
                    2 -> getFunSpinner(30)
                }

            }
        }
    }

    private fun getFunSpinner(daysAgo : Int?) {
        if (daysAgo == null) {
            salesListFiltered = salesList
            val chronopletReturn =  UtilsCharts.graphAnyChartMapChronopleth(salesListFiltered)
            changeViews(chronopletReturn)
        } else {
            val dateStart = UtilsGeneral.getDateStartToFilter(daysAgo)
            salesListFiltered = UtilsGeneral.filterList(salesList, dateStart)
            val chronopletReturn =  UtilsCharts.graphAnyChartMapChronopleth(salesListFiltered)
            changeViews(chronopletReturn)
        }
    }

    private fun changeViews(chronopletReturn: ChronopletReturn){
        val nbMax = chronopletReturn.max
        if(nbMax == 0) {
            mView.no_sales_map.visibility = View.VISIBLE
            mView.anyChartViewCountry.visibility = View.GONE
            mView.rcv_country.visibility = View.GONE
        } else {
            mView.no_sales_map.visibility = View.GONE
            mView.anyChartViewCountry.visibility = View.VISIBLE
            mView.rcv_country.visibility = View.VISIBLE
            val listWorldChrono = chronopletReturn.listEntry
            serieChronoplet.data(listWorldChrono)
            val nbMax2 = chronopletReturn.max2
            val rangesScript = UtilsCharts.getRanges(nbMax, nbMax2)
            ordinalColor.ranges(rangesScript)
            adapter.notifyDataChanged(chronopletReturn.listCountry)
        }
    }

    private fun configureRcv(listCountry : List<Country>){
        adapter = CountryAdapter(listCountry)
        mView.rcv_country.adapter = adapter
        mView.rcv_country.layoutManager = LinearLayoutManager(requireContext())
    }



    private fun initChronopleth() {

        APIlib.getInstance().setActiveAnyChartView(mView.anyChartViewCountry)

        val map = AnyChart.map()

        map.unboundRegions()
            .enabled(true)
            .fill(SolidFill("#E1E1E1", 1))
            .stroke("#D2D2D2")

        map.geoData("anychart.maps.world_source")

        val colorRange: ColorRange = map.colorRange()
        colorRange.enabled(true)
            .colorLineSize(10)
            .stroke("#B9B9B9")
            .labels("{ 'padding': 3 }")
            .labels("{ 'size': 7 }")
        colorRange.ticks()
            .enabled(true)
            .stroke("#B9B9B9")
            .position(SidePosition.OUTSIDE)
            .length(10)
        colorRange.minorTicks()
            .enabled(true)
            .stroke("#B9B9B9")
            .position("outside")
            .length(5)

        map.interactivity().selectionMode(SelectionMode.NONE)
        map.padding(0, 0, 0, 0)
        val chronopletReturn =  UtilsCharts.graphAnyChartMapChronopleth(salesListFiltered)
        val listWorldChrono = chronopletReturn.listEntry
        val nbMax = chronopletReturn.max
        val nbMax2 = chronopletReturn.max2
        configureRcv(chronopletReturn.listCountry)
        serieChronoplet  = map.choropleth(listWorldChrono)

        ordinalColor = OrdinalColor.instantiate()
        ordinalColor.colors(arrayOf("#FFFFFF", "#86DDDD", "#59C9DD", "#4688CF", "#1740D5", "#02448A"))
        val rangesScript = UtilsCharts.getRanges(nbMax, nbMax2)
        ordinalColor.ranges(rangesScript)

        map.colorRange("{orientation: 'top'}")
        serieChronoplet.colorScale(ordinalColor)

        serieChronoplet.hovered()
            .fill("#f48fb1")
            .stroke("#f99fb9")
        serieChronoplet.selected()
            .fill("#c2185b")
            .stroke("#c2185b")
        serieChronoplet.labels().enabled(true)
        serieChronoplet.labels().fontSize(10)
        serieChronoplet.labels().fontColor("#212121")
        serieChronoplet.labels().format(" ");

        mView.anyChartViewCountry.setZoomEnabled(true)
        val urlWorld = "https://cdn.anychart.com/releases/8.9.0/geodata/custom/world_source/world_source.js"
        val urlProJS = "https://cdnjs.cloudflare.com/ajax/libs/proj4js/2.3.15/proj4.js"
        mView.anyChartViewCountry.addScript(urlWorld)
        mView.anyChartViewCountry.addScript(urlProJS)
        mView.anyChartViewCountry.setChart(map)
    }

}
