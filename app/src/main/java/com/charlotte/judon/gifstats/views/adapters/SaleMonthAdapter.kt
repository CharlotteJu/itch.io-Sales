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

class SaleMonthAdapter (private var listSales : List<MonthSale>, private var onClickMonthItem: OnClickMonthItem, private val context : Context) : RecyclerView.Adapter<SaleMonthAdapter.SaleMonthViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleMonthViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_month_sale, parent, false)
        return SaleMonthViewHolder(view, onClickMonthItem, context)
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

    class SaleMonthViewHolder(itemView: View, private var onClickMonthItem: OnClickMonthItem, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {

        fun configureDesign(monthSale: MonthSale) {

            itemView.month_txt.text = getStringMonth(monthSale.month)
            itemView.nb_vente_txt.text = monthSale.nbVente.toString()
            itemView.total_txt.text = monthSale.totalPrice.toString()

            if (monthSale.isFinish) {
                itemView.logo_time_ic.setImageResource(R.drawable.ic_baseline_time_green_24)
            } else {
                itemView.logo_time_ic.setImageResource(R.drawable.ic_baseline_time_red_24)
            }

            itemView.setOnClickListener {
                onClickMonthItem.onClickMonthItem(monthSale.list)
            }
        }

        private fun getStringMonth(month : String) : String {
            val listMonth = context.resources.getStringArray(R.array.list_month)
            when (month) {
                "Jan" -> return listMonth[0]
                "Feb" -> return listMonth[1]
                "Mar" -> return listMonth[2]
                "Apr" -> return listMonth[3]
                "May" -> return listMonth[4]
                "Jun" -> return listMonth[5]
                "Jul" -> return listMonth[6]
                "Aug" -> return listMonth[7]
                "Sep" -> return listMonth[8]
                "Oct" -> return listMonth[9]
                "Nov" -> return listMonth[10]
                "Dec" -> return listMonth[11]
                else -> return month
            }
        }
    }



    interface OnClickMonthItem{
        fun onClickMonthItem(saleList : List<Sale>)
    }

}
