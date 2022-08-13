package com.kigya.quaso.views.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kigya.quaso.databinding.ItemRegionBinding
import com.kigya.quaso.model.region.Region

/**
 * Adapter for displaying the list of available regions
 */
class RegionsAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<RegionsAdapter.Holder>(), View.OnClickListener {

    /**
     * List of regions to display.
     */
    var items: List<Region> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new [RecyclerView.ViewHolder] of the given type to represent an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRegionBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return Holder(binding)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the RecyclerView.ViewHolder.itemView to reflect
     * the item at the given position.
     */
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val region = items[position]
        with(holder.binding) {
            root.tag = region
            regionNameTextView.text = region.nameFull
            regionTextInside.text = region.nameCut
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int = items.size

    /**
     * Called when a view has been clicked.
     */
    override fun onClick(v: View) {
        val item = v.tag as Region
        listener.onRegionChosen(item)
    }

    /**
     * ViewHolder for the [RegionsAdapter].
     */
    class Holder(
        val binding: ItemRegionBinding
    ) : RecyclerView.ViewHolder(binding.root)

    /**
     * Listener for when a region is chosen.
     */
    interface Listener {
        /**
         * Called when user chooses the specified region
         */
        fun onRegionChosen(region: Region)
    }
}