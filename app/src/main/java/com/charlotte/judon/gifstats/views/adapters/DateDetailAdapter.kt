package com.charlotte.judon.gifstats.views.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.DateDetail
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_graphs.view.*
import kotlinx.android.synthetic.main.item_date_detail.view.*

class DateDetailAdapter(private var listDateDetail : List<DateDetail>) : RecyclerView.Adapter<DateDetailAdapter.DateDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_date_detail, parent, false)
        return DateDetailViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: DateDetailViewHolder, position: Int) {
        val dateDetail = this.listDateDetail[position]
        holder.configureDesign(dateDetail)
    }

    override fun getItemCount(): Int {
        return this.listDateDetail.size
    }

    fun notifyDataChanged(listDateDetail: List<DateDetail>) {
        this.listDateDetail = listDateDetail
        this.notifyDataSetChanged()
    }


    class DateDetailViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun configureDesign(dateDetail: DateDetail) {
            itemView.item_graph_title.text = dateDetail.day
            itemView.item_graph_total.text = dateDetail.totalSale.toString()

            if(dateDetail.totalSale > 0 ){
                itemView.graphTemplate.visibility = View.VISIBLE
                itemView.no_sales.visibility = View.INVISIBLE

                val barDataSet = BarDataSet(dateDetail.listEntry, "")
                barDataSet.color = Color.parseColor("#F80039")
                barDataSet.highLightColor = Color.parseColor("#4B2E5A")
                barDataSet.setDrawValues(false)
               // val formatterX = IndexAxisValueFormatter(dateDetail.listString)
                itemView.graphTemplate.data = BarData(barDataSet)
                itemView.graphTemplate.description.isEnabled = false
                itemView.graphTemplate.legend.isEnabled = false
                itemView.graphTemplate.xAxis.setDrawGridLines(false)
                itemView.graphTemplate.axisLeft.setDrawGridLines(false)
                itemView.graphTemplate.xAxis.position = XAxis.XAxisPosition.BOTTOM
                //itemView.graphTemplate.xAxis.valueFormatter = formatterX
                itemView.graphTemplate.axisLeft.axisMinimum = 0f
                itemView.graphTemplate.axisRight.axisMinimum = 0f
                itemView.graphTemplate.xAxis.mAxisMinimum = 0f
                itemView.graphTemplate.xAxis.mAxisMaximum = 23f
            }
            else {
                itemView.graphTemplate.visibility = View.GONE
                itemView.no_sales.visibility = View.VISIBLE
            }


        }
    }


}