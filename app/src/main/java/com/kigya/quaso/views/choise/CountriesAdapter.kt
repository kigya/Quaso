package com.kigya.quaso.views.choise

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.kigya.foundation.model.takeSuccess
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.quaso.databinding.ItemCountryBinding
import com.kigya.quaso.model.countries.Country

class CountriesAdapter(
    private val listener: Listener,
    private val resources: Resources
) : RecyclerView.Adapter<CountriesAdapter.Holder>(), View.OnClickListener,
    SearchView.OnQueryTextListener, Filterable{

    var items: List<Country> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var itemsFiltered: List<Country> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCountryBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val country = itemsFiltered[position]
        with(holder.binding) {
            root.tag = country
            countryTextInside.text = resources.getString(country.name)
        }
    }

    override fun getItemCount(): Int = itemsFiltered.size

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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val filter = items.filter { resources.getString(it.name).contains(newText!!, true) }
        items = filter
        return true
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                itemsFiltered = if (charString.isEmpty()) items else {
                    val filteredList = ArrayList<Country>()
                    items
                        .filter {
                            resources.getString(it.name).contains(charString, true)

                        }
                        .forEach { filteredList.add(it) }
                    filteredList

                }
                return FilterResults().apply { values = itemsFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                itemsFiltered = if (results?.values == null)
                    emptyList()
                else
                    results.values as List<Country>
                notifyDataSetChanged()
            }
        }
    }
}