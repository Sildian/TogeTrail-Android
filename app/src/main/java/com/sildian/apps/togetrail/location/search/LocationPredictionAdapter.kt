package com.sildian.apps.togetrail.location.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Displays information about locations previsions
 ************************************************************************************************/

class LocationPredictionAdapter (
    private val locationsPredictions:List<AutocompletePrediction>,
    private val onLocationClickListener: LocationPredictionViewHolder.OnLocationClickListener
)
    : RecyclerView.Adapter<LocationPredictionViewHolder> ()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationPredictionViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_recycler_view_location_prediction, parent, false)
        return LocationPredictionViewHolder(
            view,
            this.onLocationClickListener
        )
    }

    override fun getItemCount(): Int {
        return this.locationsPredictions.size
    }

    override fun onBindViewHolder(holder: LocationPredictionViewHolder, position: Int) {
        holder.updateUI(this.locationsPredictions[position])
    }
}