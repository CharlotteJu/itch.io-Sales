package com.charlotte.judon.gifstats.views.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.Utils
import kotlinx.android.synthetic.main.item_sale.view.*

class SaleAdapter (private var listSales : List<Sale>, private val context: Context) : RecyclerView.Adapter<SaleAdapter.SaleViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_sale, parent, false)
        return SaleViewHolder(
            view,
            listSales,
            context
        )
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = this.listSales[position]
        holder.configureDesign(sale)
    }


    override fun getItemCount(): Int {
        return this.listSales.size
    }

    fun notify (listSales: List<Sale>) {
        this.listSales = listSales
        this.notifyDataSetChanged()
    }

    class SaleViewHolder (itemView : View, private var listSales: List<Sale>, private val context: Context) : RecyclerView.ViewHolder(itemView) {

        fun configureDesign(sale : Sale) {
            if (sale.currency == "USD") {
                itemView.original_price_content.text = "${Utils.convertDollarToEuros(sale.amount)} €"
                itemView.encaisse_price_content.text = "${Utils.convertDollarToEuros(sale.amountDelivered)} €"
            } else {
                itemView.original_price_content.text = "${sale.amount} €"
                itemView.encaisse_price_content.text = "${sale.amountDelivered} €"
            }

            itemView.date.text = sale.dateString

            val dateString = Utils.convertStringToDateWithLocale(sale.dateString, sale.hour,"MM/dd")
            itemView.date.text = dateString[0]
            itemView.hour.text = dateString[1]
            itemView.country_code.text = sale.countryCode

            if (sale.tip > 0.0)
            {
                itemView.tips.visibility = View.VISIBLE
                itemView.tips.text = sale.tip.toString()
            }
            else
            {
                itemView.tips.visibility = View.GONE
            }
        }
    }


}