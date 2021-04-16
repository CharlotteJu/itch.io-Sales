package com.charlotte.judon.gifstats.utils

import android.content.Context
import com.charlotte.judon.gifstats.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.item_date_detail.view.*
import kotlinx.android.synthetic.main.test_marker.view.*
import java.text.DecimalFormat

class CustomMarker(context: Context) : MarkerView(context, R.layout.test_marker) {


    override fun refreshContent(e: Entry, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        marker_text_hour.text = e.x.toInt().toString() + "h // " + e.y.toInt().toString()
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width/1.5).toFloat(), -(height).toFloat())
    }
}