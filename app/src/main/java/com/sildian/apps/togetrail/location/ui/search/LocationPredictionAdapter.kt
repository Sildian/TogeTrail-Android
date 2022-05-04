package com.sildian.apps.togetrail.location.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewLocationPredictionBinding

/*************************************************************************************************
 * Displays information about locations predictions
 ************************************************************************************************/

class LocationPredictionAdapter (
    private val locationsPredictions: List<AutocompletePrediction>,
    private val onLocationClickListener: OnLocationClickListener
)
    : RecyclerView.Adapter<LocationPredictionAdapter.LocationPredictionViewHolder> ()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationPredictionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewLocationPredictionBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_recycler_view_location_prediction, parent, false)
        return LocationPredictionViewHolder(binding, this.onLocationClickListener)
    }

    override fun getItemCount(): Int {
        return this.locationsPredictions.size
    }

    override fun onBindViewHolder(holder: LocationPredictionViewHolder, position: Int) {
        holder.bind(this.locationsPredictions[position])
    }

    /***********************************Callback*************************************************/

    interface OnLocationClickListener {
        fun onLocationClick(placeId:String)
    }

    /***********************************ViewHolder***********************************************/

    class LocationPredictionViewHolder(
        private val binding: ItemRecyclerViewLocationPredictionBinding,
        private val onLocationClickListener: OnLocationClickListener
    )
        : RecyclerView.ViewHolder(binding.root) {

        /***************************************Data*************************************************/

        private var locationPrediction: AutocompletePrediction? = null
        private var locationName: String? = null

        /**********************************Initialization********************************************/

        init {
            this.binding.locationPredictionViewHolder = this
        }

        /**********************************UI monitoring*********************************************/

        fun bind(locationPrediction: AutocompletePrediction) {
            this.locationPrediction = locationPrediction
            this.locationName = this.locationPrediction?.getFullText(null).toString()
            this.binding.locationName = this.locationName
        }

        @Suppress("UNUSED_PARAMETER")
        fun onLocationPredictionClick(view: View) {
            this.locationPrediction?.placeId?.let { placeId ->
                this.onLocationClickListener.onLocationClick(placeId)
            }
        }
    }
}