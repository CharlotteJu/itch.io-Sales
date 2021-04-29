package com.charlotte.judon.gifstats.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.CustomCountry
import kotlinx.android.synthetic.main.item_country.view.*

/**
 * Class used to populate a [RecyclerView] in [views/fragments/MapFragment.kt]
 * Based on [CustomCountry]
 * @author Charlotte JUDON
 */
class CountryAdapter(private var listCustomCountry : List<CustomCountry>) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = this.listCustomCountry[position]
        holder.configureDesign(country)
    }

    override fun getItemCount(): Int {
        return listCustomCountry.size
    }

    fun notifyDataChanged(listCustomCountry: List<CustomCountry>) {
        this.listCustomCountry = listCustomCountry
        this.notifyDataSetChanged()
    }

    class CountryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun configureDesign(customCountry : CustomCountry) {
            itemView.country.text = customCountry.country
            itemView.number.text = customCountry.nb.toString()
        }
    }
}