package com.sildian.apps.togetrail.location.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.android.synthetic.main.item_recycler_view_location_prediction.view.*

/*************************************************************************************************
 * Displays information about a location prevision
 ************************************************************************************************/

class LocationPredictionViewHolder(
    itemView:View,
    private val onLocationClickListener: OnLocationClickListener
)
    : RecyclerView.ViewHolder(itemView) {

    /***********************************Callback*************************************************/

    interface OnLocationClickListener{
        fun onLocationClick(placeId:String)
    }

    /***************************************Data*************************************************/

    private var locationPrediction:AutocompletePrediction?=null

    /**********************************UI component**********************************************/

    private val locationPredictionText by lazy {itemView.item_recycler_view_location_prediction_text}

    /**********************************Initialization********************************************/

    init{
        this.itemView.setOnClickListener {
            if(this.locationPrediction!=null) {
                this.onLocationClickListener.onLocationClick(this.locationPrediction?.placeId!!)
            }
        }
    }

    /**********************************UI monitoring*********************************************/

    fun updateUI(locationPrediction: AutocompletePrediction){
        this.locationPrediction=locationPrediction
        this.locationPredictionText.text=this.locationPrediction?.getFullText(null).toString()
    }
}