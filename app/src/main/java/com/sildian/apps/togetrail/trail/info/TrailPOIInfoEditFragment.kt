package com.sildian.apps.togetrail.trail.info

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sdsmdg.harjot.crollerTest.Croller

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.NumberUtilities
import com.sildian.apps.togetrail.trail.model.TrailPointOfInterest
import kotlinx.android.synthetic.main.fragment_trail_poi_info.view.*
import kotlinx.android.synthetic.main.fragment_trail_poi_info_edit.view.*

/*************************************************************************************************
 * Allows to edit information about a trailPointOfInterest
 * @param trailPointOfInterest : the related trailPointOfInterest
 ************************************************************************************************/

class TrailPOIInfoEditFragment(val trailPointOfInterest: TrailPointOfInterest?=null) :
    Fragment(),
    Croller.onProgressChangedListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        const val TAG_FRAGMENT="TAG_FRAGMENT"

        /**Values max**/
        private const val VALUE_MAX_ALTITUDE=4000       //Max value for an altitude (in meters)
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val nameTextField by lazy {layout.fragment_trail_poi_info_edit_text_field_name}
    private val metricsCroller by lazy {layout.fragment_trail_poi_info_edit_croller_metrics}
    private val elevationText by lazy {layout.fragment_trail_poi_info_edit_text_elevation}
    private val descriptionTextField by lazy {layout.fragment_trail_poi_info_edit_text_field_description}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout= inflater.inflate(R.layout.fragment_trail_poi_info_edit, container, false)
        initializeAllUIComponents()
        return this.layout
    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeNameTextField()
        initializeMetricsCroller()
        initializeElevationText()
        initializeDescriptionTextField()
    }

    private fun initializeNameTextField(){
        this.nameTextField.setText(this.trailPointOfInterest?.name)
    }

    private fun initializeMetricsCroller(){
        val elevation=this.trailPointOfInterest?.elevation
        updateCroller(elevation)
        this.metricsCroller.setOnProgressChangedListener(this)
    }

    private fun initializeElevationText(){
        val elevation=this.trailPointOfInterest?.elevation
        updateElevation(elevation)
    }

    private fun initializeDescriptionTextField(){
        this.descriptionTextField.setText(this.trailPointOfInterest?.description)
    }

    /*****************************Metrics monitoring with croller*********************************/

    override fun onProgressChanged(progress: Int) {
        updateElevation(progress)
    }

    private fun updateCroller(currentValue:Int?){
        this.metricsCroller.max= VALUE_MAX_ALTITUDE
        this.metricsCroller.progress=currentValue?:0
    }

    private fun updateElevation(elevation:Int?){
        this.trailPointOfInterest?.elevation=elevation
        val metric=resources.getString(R.string.metric_meter)
        val suffix=resources.getString(R.string.label_trail_poi_elevation)
        val unknownText=resources.getString(R.string.message_unknown_short)
        val elevationToDisplay=
            if(elevation!=null)
                NumberUtilities.displayNumberWithMetricAndSuffix(
                    elevation.toDouble(), 0, metric, suffix, true)
            else unknownText
        this.elevationText.text=elevationToDisplay
        val ascentCroller =
            NumberUtilities.displayNumberWithMetric(elevation?.toDouble()?:0.toDouble(), 0, metric)
        this.metricsCroller.label = ascentCroller
    }
}
