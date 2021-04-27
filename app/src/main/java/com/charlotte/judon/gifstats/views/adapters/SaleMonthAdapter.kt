package com.charlotte.judon.gifstats.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.MonthSale
import com.charlotte.judon.gifstats.model.Sale
import kotlinx.android.synthetic.main.item_month_sale.view.*

class SaleMonthAdapter (private var listSales : List<MonthSale>, private var onClickMonthItem: OnClickMonthItem,
                        private val context : Context, private val symbolCurrency : String)
    : RecyclerView.Adapter<SaleMonthAdapter.SaleMonthViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleMonthViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_month_sale, parent, false)
        return SaleMonthViewHolder(view, onClickMonthItem, context, symbolCurrency)
    }

    override fun onBindViewHolder(holder: SaleMonthViewHolder, position: Int) {
        val sale = this.listSales[position]
        holder.configureDesign(sale)
    }


    override fun getItemCount(): Int {
        return this.listSales.size
    }

    fun notify(listSales: List<MonthSale>) {
        this.listSales = listSales
        this.notifyDataSetChanged()
    }

    class SaleMonthViewHolder(itemView: View, private var onClickMonthItem: OnClickMonthItem,
                              private val context: Context, private val symbolCurrency : String)
        : RecyclerView.ViewHolder(itemView) {

        fun configureDesign(monthSale: MonthSale) {

            itemView.month_txt.text = "${getStringMonth(monthSale.month)} ${monthSale.year}"
            itemView.nb_vente_txt.text = monthSale.nbVente.toString()
            itemView.total_txt.text = "${monthSale.totalPrice} $symbolCurrency"

            if (monthSale.isInProgress) {
                itemView.logo_time_ic.setImageResource(R.drawable.ic_baseline_date_range_purple_24)
            } else {
                itemView.logo_time_ic.setImageResource(R.drawable.ic_baseline_date_range_green_24)
            }

            itemView.setOnClickListener {
                onClickMonthItem.onClickMonthItem(monthSale.list)
            }
        }

        private fun getStringMonth(month : String) : String {
            val listMonth = context.resources.getStringArray(R.array.list_month)
            return when (month) {
                "Jan" -> listMonth[0]
                "Feb" -> listMonth[1]
                "Mar" -> listMonth[2]
                "Apr" -> listMonth[3]
                "May" -> listMonth[4]
                "Jun" -> listMonth[5]
                "Jul" -> listMonth[6]
                "Aug" -> listMonth[7]
                "Sep" -> listMonth[8]
                "Oct" -> listMonth[9]
                "Nov" -> listMonth[10]
                "Dec" -> listMonth[11]
                else -> month
            }
        }
    }



    interface OnClickMonthItem{
        fun onClickMonthItem(saleList : List<Sale>)
    }

}
