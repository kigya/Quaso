package com.kigya.quaso.views.choise

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.quaso.databinding.ItemCountryBinding
import com.kigya.quaso.model.countries.Country

/**
 * Adapter for displaying the list of available countries
 */
class CountriesAdapter(
    private val listener: Listener,
    private val resources: Resources
) : RecyclerView.Adapter<CountriesAdapter.Holder>(), View.OnClickListener,
    SearchView.OnQueryTextListener, Filterable {

    /**
     * List of all available countries.
     */
    var items: List<Country> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * List of filtered countries to display.
     */
    var itemsFiltered: List<Country> = emptyList()

    /**
     * Called when RecyclerView needs a new [RecyclerView.ViewHolder] of the given type to represent an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCountryBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return Holder(binding)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the RecyclerView.ViewHolder.itemView to reflect
     * the item at the given position.
     */
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val country = itemsFiltered[position]
        with(holder.binding) {
            root.tag = country
            countryTextInside.text = resources.getString(country.name)
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int = itemsFiltered.size

    /**
     * Called when a view has been clicked.
     */
    override fun onClick(v: View) {
        val item = v.tag as Country
        listener.onCountryChosen(item)
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * Overrides the standard behavior by returning true
     * to indicate that it has handled the submit request.
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    /**
     * Called when the query text is changed by the user.
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        val filter = filterItems(newText)
        items = filter
        return true
    }

    /**
     * Filter the items based on the query
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                itemsFiltered = if (charString.isEmpty()) items else {
                    val filteredList = ArrayList<Country>()
                    items.filter {
                        resources.getString(it.name).contains(charString, true)
                    }.forEach { filteredList.add(it) }
                    filteredList
                }
                return FilterResults().apply { values = itemsFiltered }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                itemsFiltered = if (results?.values == null) emptyList()
                else results.values as List<Country>
                notifyDataSetChanged()
            }
        }
    }

    /**
     * Filtering items according to the actual query.
     */
    private fun filterItems(newText: String?) =
        items.filter { resources.getString(it.name).contains(newText!!, true) }

    /**
     * ViewHolder for the list item.
     */
    class Holder(
        val binding: ItemCountryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    /**
     * Listener for the list item.
     */
    interface Listener {
        /**
         * Called when user chooses the specified country.
         */
        fun onCountryChosen(country: Country)
    }
}