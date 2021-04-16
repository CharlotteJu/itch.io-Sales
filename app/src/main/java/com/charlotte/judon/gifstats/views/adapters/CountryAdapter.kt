package com.charlotte.judon.gifstats.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.Country
import kotlinx.android.synthetic.main.item_country.view.*

class CountryAdapter(private var listCountry : List<Country>) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = this.listCountry[position]
        holder.configureDesign(country)
    }

    override fun getItemCount(): Int {
        return listCountry.size
    }

    fun notifyDataChanged(listCountry: List<Country>) {
        this.listCountry = listCountry
        this.notifyDataSetChanged()
    }

    class CountryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun configureDesign(country : Country) {
            itemView.country.text = country.country
            itemView.number.text = country.nb.toString()
        }
    }
}