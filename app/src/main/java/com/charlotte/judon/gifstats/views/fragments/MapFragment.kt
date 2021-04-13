package com.charlotte.judon.gifstats.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.charts.Cartesian
import com.anychart.core.map.series.Choropleth
import com.anychart.core.ui.ColorRange
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.graphics.vector.SolidFill
import com.anychart.scales.OrdinalColor
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
    private lateinit var serieChronoplet : Choropleth
    private lateinit var ordinalColor: OrdinalColor
    //private lateinit var map : Map

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        mView = inflater.inflate(R.layout.fragment_map, container, false)

        setWorld = Set.instantiate()
        setFrance = Set.instantiate()

        configureSpinner()

        //initGraphCountry()
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
            setWorld.data(Utils.graphAnyChartMap(salesListFiltered))
            setFrance.data(Utils.getAnyChartBubbleFromFranceAndRussia(salesListFiltered))
            val chronopletReturn =  Utils.graphAnyChartMapChronopleth(salesListFiltered)
            val listWorldChrono = chronopletReturn.list
            serieChronoplet.data(listWorldChrono)
            val nbMax = chronopletReturn.max
            val nbMax2 = chronopletReturn.max2
            val rangesScript = getRanges(nbMax, nbMax2)
            ordinalColor.ranges(rangesScript)


        } else {
            val dateStart = Utils.getDateStartToFilter(daysAgo)
            salesListFiltered = Utils.filterList(salesList, dateStart)
            setWorld.data(Utils.graphAnyChartMap(salesListFiltered))
            setFrance.data(Utils.getAnyChartBubbleFromFranceAndRussia(salesListFiltered))
            val chronopletReturn =  Utils.graphAnyChartMapChronopleth(salesListFiltered)
            val listWorldChrono = chronopletReturn.list
            serieChronoplet.data(listWorldChrono)
            val nbMax = chronopletReturn.max
            val nbMax2 = chronopletReturn.max2
            val rangesScript = getRanges(nbMax, nbMax2)
            ordinalColor.ranges(rangesScript)

        }
    }

    private fun initGraphCountry() {
        APIlib.getInstance().setActiveAnyChartView(mView.anyChartViewCountry)
        val listWorld = Utils.graphAnyChartMap(salesListFiltered)
        val listFranceAndRussia = Utils.getAnyChartBubbleFromFranceAndRussia(salesListFiltered)

        val bubbleMap = AnyChart.bubbleMap()
        //bubbleMap.geoData("anychart.maps.world_source")
        bubbleMap.geoData("anychart.maps.world")

        setWorld.data(listWorld)
        val mapWorld = setWorld.mapAs("{ 'id': 'x', 'size': 'value' }")
        val settingsWorld = bubbleMap.bubble(mapWorld)
        settingsWorld.color("#F80039")
        settingsWorld.name("Monde")
        settingsWorld.legendItem("{ iconType: 'circle' }")


        setFrance.data(listFranceAndRussia)
        val mapFrance = setFrance.mapAs("{ 'id': 'x', 'size': 'value' }")
        val settingsFrance = bubbleMap.bubble(mapFrance)
        settingsFrance.color("#4B2E5A")
        settingsFrance.name("France and Russia")
        settingsFrance.legendItem("{ iconType: 'circle' }")

        bubbleMap.tooltip()
            .useHtml(true)
            .title("{ fontColor: '#7c868e' }")
            .format(
                "function() {\n" +
                        "          var span_for_value = '<span style=\"color: #FFFFFF; font-size: 12px; font-weight: bold\">';\n" +
                        "          var span_for_description = '<br/><span style=\"color: #FFFFFF; font-size: 12px; font-style: italic\">';\n" +
                        "          if (this.getData('value') > 0) {\n" +
                        "            return " +
                        "               span_for_value + this.getData('x') + ' </span></strong>' +\n" +
                        "               span_for_description + this.getData('value') + ' </span>';\n" +
                        "          }" +
                        "        }"
            )

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

        val urlWorld = "https://cdn.anychart.com/releases/8.9.0/geodata/custom/world_source/world_source.js"
        val urlProJS = "https://cdnjs.cloudflare.com/ajax/libs/proj4js/2.3.15/proj4.js"
        val world = "https://cdn.anychart.com/releases/8.9.0/geodata/custom/world/world.js"
        mView.anyChartViewCountry.addScript(world)
        mView.anyChartViewCountry.addScript(urlProJS)

        mView.anyChartViewCountry.setChart(bubbleMap)
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
        val chronopletReturn =  Utils.graphAnyChartMapChronopleth(salesListFiltered)
        val listWorldChrono = chronopletReturn.list
        val nbMax = chronopletReturn.max
        val nbMax2 = chronopletReturn.max2
        serieChronoplet  = map.choropleth(listWorldChrono)
        Log.d("DEBUG_APP", "MAX : ${chronopletReturn.max} //// MAX2 : ${chronopletReturn.max2}")

        //val linearColor = LinearColor.instantiate()
        //val series: Choropleth = map.choropleth(listWorldChrono)
        //linearColor.colors(arrayOf("#C2E6EA", "#09C5DC", "#219FDA", "#1A71CF","#0263CC","#0D42C8" ,"#013ACA", "#0104CA", "#02048F"))
        //series.colorScale(linearColor)

        ordinalColor = OrdinalColor.instantiate()
        //ordinalColor.colors(arrayOf("#FFFFFF", "#86DDDD", "#59C9DD", "#4688CF", "#1740D5", "#02448A"))
        ordinalColor.colors(arrayOf("#FFFFFF", "#E8959E", "#E65B6A", "#F80039", "#C3012E", "#7A011D"))
        val rangesScript = getRanges(nbMax, nbMax2)
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

    private fun getRanges(nbMax : Int, nbMax2 : Int) : Array<String> {
        var nb1 = 0
        var nb2 = 0
        var nb3 = 0
        var nb4 = 0

        Log.d("DEBUG_APP", "MAX : $nbMax //// MAX2 : $nbMax2 ")

        if (nbMax<10) {
            return arrayOf("{less: 1}", "{greater: 1}")
        }

        val diffMax = nbMax - nbMax2

        if (diffMax > nbMax/10) {
            nb1 = nbMax2/4
            nb2 = (nbMax2/4) *2
            nb3 = (nbMax2/4) *3
            nb4 = nbMax2
            Log.d("DEBUG_APP", "BEAUCOUP DE DIFFERENCE : 1 : $nb1 //// 2 : $nb2 //// 3 : $nb3 //// 4 : $nb4 ")
        }
        else {
            nb1 = nbMax/5
            nb2 = (nbMax/5) *2
            nb3 = (nbMax/5) *3
            nb4 = (nbMax/4) *3
            Log.d("DEBUG_APP", "PETITE DIFFERENCE : 1 : $nb1 //// 2 : $nb2 //// 3 : $nb3 //// 4 : $nb4 ")
        }

        return arrayOf("{less: 1}", "{from: 1, to: $nb1}", "{from: $nb1, to: $nb2}", "{from: $nb2, to: $nb3}", "{from: $nb3, to: $nb4}", "{greater: $nb4}")
    }

}

