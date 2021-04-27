package com.charlotte.judon.gifstats.views.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.CustomCurrency
import com.charlotte.judon.gifstats.model.Sale
import com.charlotte.judon.gifstats.utils.UtilsGeneral
import com.charlotte.judon.gifstats.utils.UtilsCurrency
import kotlinx.android.synthetic.main.item_sale.view.*

class SaleAdapter (private var listSales : List<Sale>,
                   private val currentCurrency: CustomCurrency,
                   private val listCurrencies : List<CustomCurrency>,
                   private val dateFormat : String)
    : RecyclerView.Adapter<SaleAdapter.SaleViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_sale, parent, false)
        return SaleViewHolder(
            view, currentCurrency, listCurrencies, dateFormat)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = this.listSales[position]
        holder.configureDesign(sale)
    }


    override fun getItemCount(): Int {
        return this.listSales.size
    }

    class SaleViewHolder (itemView : View,
                          private val currentCurrency: CustomCurrency,
                          private val listCurrencies : List<CustomCurrency>,
                          private val dateFormat : String)
        : RecyclerView.ViewHolder(itemView) {

        fun configureDesign(sale : Sale) {

            val priceNet = if(sale.currency == currentCurrency.code) {
                sale.amountDelivered
            } else {
                UtilsCurrency.convertPriceBetweenTwoCurrencies(sale.currency, currentCurrency, sale.amountDelivered, listCurrencies)
            }

            val priceBrut = if(sale.currency == currentCurrency.code) {
                sale.amount
            } else {
                UtilsCurrency.convertPriceBetweenTwoCurrencies(sale.currency, currentCurrency, sale.amount, listCurrencies)
            }

            itemView.original_price_content.text = "$priceBrut"
            itemView.encaisse_price_content.text = "$priceNet ${currentCurrency.symbol}"

            itemView.date.text = sale.dateString

            val dateString = UtilsGeneral.convertStringToDateWithLocale(sale.dateString, sale.hour,dateFormat)
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