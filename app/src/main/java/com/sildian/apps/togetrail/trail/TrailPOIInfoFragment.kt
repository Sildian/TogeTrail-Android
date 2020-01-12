package com.sildian.apps.togetrail.trail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.NumberUtilities
import com.sildian.apps.togetrail.trail.model.TrailPointOfInterest
import kotlinx.android.synthetic.main.fragment_trail_poi_info.view.*

/*************************************************************************************************
 * Shows information about a point of interest
 * This fragment should be used as a nested fragment within a BottomSheet
 * @param trailPointOfInterest : the related point of interest
 ************************************************************************************************/

class TrailPOIInfoFragment (val trailPointOfInterest:TrailPointOfInterest?=null) : Fragment() {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        const val TAG_FRAGMENT="TAG_FRAGMENT"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val nameText by lazy {layout.fragment_trail_poi_info_text_name}
    private val elevationText by lazy {layout.fragment_trail_poi_info_text_elevation}
    private val descriptionText by lazy {layout.fragment_trail_poi_info_text_description}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_trail_poi_info, container, false)
        initializeAllUIComponents()
        return this.layout
    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeNameText()
        initializeElevationText()
        initializeDescriptionText()
    }

    private fun initializeNameText(){
        this.nameText.text=this.trailPointOfInterest?.name
    }

    private fun initializeElevationText(){
        val elevation=this.trailPointOfInterest?.elevation
        val metric=resources.getString(R.string.metric_meter)
        val suffix=resources.getString(R.string.label_trail_poi_elevation)
        val unkownText=resources.getString(R.string.message_unknown)
        this.elevationText.text=
            if(elevation!=null)
                NumberUtilities.displayNumberWithMetricAndSuffix(
                    elevation.toDouble(), 0, metric, suffix, false)
            else unkownText
    }

    private fun initializeDescriptionText(){
        val description=this.trailPointOfInterest?.description
        if(!description.isNullOrEmpty()){
            this.descriptionText.text=description
        }else{
            this.descriptionText.setText(R.string.message_no_description_available)
        }
    }
}
