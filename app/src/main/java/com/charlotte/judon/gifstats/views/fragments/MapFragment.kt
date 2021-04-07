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
import com.anychart.enums.*
import com.anychart.graphics.vector.SolidFill
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.Utils
import kotlinx.android.synthetic.main.fragment_map.view.*


class MapFragment : Fragment() {
    private lateinit var salesList : List<Sale>
    private lateinit var mView: View
    private lateinit var salesListFiltered : List<Sale>
    private lateinit var setWorld : Set
    private lateinit var setFrance : Set

    private lateinit var chartVertical : Cartesian

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        mView = inflater.inflate(R.layout.fragment_map, container, false)

        setWorld = Set.instantiate()
        setFrance = Set.instantiate()

        configureSpinner()
        initGraphCountry()


        return mView
    }

    private fun configureSpinner() {
        val spinnerFilter = resources.getStringArray(R.array.spinner_filter)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerFilter)
        mView.spinner.adapter = adapter

        mView.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                when (position) {
                    0 -> {
                        salesListFiltered = salesList
                        setWorld.data(Utils.graphAnyChartMap(salesListFiltered))
                        setFrance.data(Utils.graphAnyChartMapFRANCE(salesListFiltered))
                    }
                    1 -> {
                        val dateStart = Utils.getDateStartToFilter(7)
                        salesListFiltered = Utils.filterList(salesList, dateStart)
                        setWorld.data(Utils.graphAnyChartMap(salesListFiltered))
                        setFrance.data(Utils.graphAnyChartMapFRANCE(salesListFiltered))
                    }
                    2 -> {
                        val dateStart = Utils.getDateStartToFilter(30)
                        salesListFiltered = Utils.filterList(salesList, dateStart)
                        setWorld.data(Utils.graphAnyChartMap(salesListFiltered))
                        setFrance.data(Utils.graphAnyChartMapFRANCE(salesListFiltered))
                    }
                }

            }
        }
    }

    private fun initGraphCountry() {
        APIlib.getInstance().setActiveAnyChartView(mView.anyChartViewCountry)
        val listWorld = Utils.graphAnyChartMap(salesListFiltered)
        val listFrance = Utils.graphAnyChartMapFRANCE(salesListFiltered)

        val bubbleMap = AnyChart.bubbleMap()
        bubbleMap.geoData("anychart.maps.world_source")

        setWorld.data(listWorld)
        val mapWorld = setWorld.mapAs("{ 'id': 'x', 'size': 'value' }")
        val settingsWorld = bubbleMap.bubble(mapWorld)
        settingsWorld.name("Monde")
        settingsWorld.legendItem("{ iconType: 'circle' }")


        setFrance.data(listFrance)
        val mapFrance = setFrance.mapAs("{ 'id': 'x', 'size': 'value' }")
        val settingsFrance = bubbleMap.bubble(mapFrance)
        settingsFrance.color("#FF5722")
        settingsFrance.name("France")
        settingsFrance.legendItem("{ iconType: 'circle' }")

        bubbleMap.tooltip()
            .useHtml(true)
            .title("{ fontColor: '#7c868e' }")
            .format("function() {\n" +
                    "          var span_for_value = '<span style=\"color: #FFFFFF; font-size: 12px; font-weight: bold\">';\n" +
                    "          var span_for_description = '<br/><span style=\"color: #FFFFFF; font-size: 12px; font-style: italic\">';\n" +
                    "          if (this.getData('value') > 0) {\n" +
                    "            return " +
                    "               span_for_value + this.getData('x') + ' </span></strong>' +\n" +
                    "               span_for_description + this.getData('value') + ' </span>';\n" +
                    "          }" +
                    "        }")

        bubbleMap.tooltip().background()
            .enabled(true)
            .fill(SolidFill("#fff", 1))
            .stroke("#c1c1c1")
            .corners("3")
            .cornerType(BackgroundCornersType.ROUND)

        bubbleMap.legend()
            .enabled(true)
            .position("center-top")
            .align(Align.CENTER)
            .itemsLayout(LegendLayout.HORIZONTAL)
            .padding(0, 0, 30, 0)
            .paginator(false);

        bubbleMap.interactivity().selectionMode(SelectionMode.NONE)

        mView.anyChartViewCountry.setZoomEnabled(true)
        mView.anyChartViewCountry.addScript("https://cdn.anychart.com/releases/8.9.0/geodata/custom/world_source/world_source.js")

        mView.anyChartViewCountry.setChart(bubbleMap)
    }



}