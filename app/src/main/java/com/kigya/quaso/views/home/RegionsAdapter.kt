package com.kigya.quaso.views.home

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

    var items: List<Region> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRegionBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val region = items[position]
        with(holder.binding) {
            root.tag = region
            regionNameTextView.text = region.name
            regionTextInside.text = region.nameCut
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onClick(v: View) {
        val item = v.tag as Region
        listener.onRegionChosen(item)
    }

    class Holder(
        val binding: ItemRegionBinding
    ) : RecyclerView.ViewHolder(binding.root)

    interface Listener {
        /**
         * Called when user chooses the specified region
         */
        fun onRegionChosen(region: Region)
    }
}