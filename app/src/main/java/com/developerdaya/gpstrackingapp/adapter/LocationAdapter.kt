package com.developerdaya.gpstrackingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.developerdaya.gpstrackingapp.databinding.LocationItemBinding
import com.developerdaya.gpstrackingapp.model.LocationData

class LocationAdapter : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    private var locationList: List<LocationData> = emptyList()

    // ViewHolder class
    inner class LocationViewHolder(private val binding: LocationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(location: LocationData) {
            binding.latitudeText2.text = "${location.latitude}"
            binding.longitudeText2.text = "${location.longitude}"
            binding.timestampText2.text = "${location.timestamp}"
            binding.mLocation2.text = "${location.address}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = LocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]
        holder.bind(location)
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    // Update the list of locations
    fun submitList(locations: List<LocationData>) {
        locationList = locations
        notifyDataSetChanged()
    }
}
