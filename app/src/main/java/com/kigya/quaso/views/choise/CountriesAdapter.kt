package com.kigya.quaso.views.choise

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.kigya.foundation.model.takeSuccess
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.quaso.databinding.ItemCountryBinding
import com.kigya.quaso.model.countries.Country

class CountriesAdapter(
    private val listener: Listener,
    private val resources: Resources
) : RecyclerView.Adapter<CountriesAdapter.Holder>(), View.OnClickListener {

    var items: List<Country> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCountryBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val country = items[position]
        with(holder.binding) {
            root.tag = country
            countryTextInside.text = resources.getString(country.name)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onClick(v: View) {
        val item = v.tag as Country
        listener.onCountryChosen(item)
    }

    class Holder(
        val binding: ItemCountryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    interface Listener {
        /**
         * Called when user chooses the specified region
         */
        fun onCountryChosen(country: Country)
    }
}