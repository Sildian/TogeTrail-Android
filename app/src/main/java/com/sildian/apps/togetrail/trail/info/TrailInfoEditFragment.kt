package com.sildian.apps.togetrail.trail.info

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.sdsmdg.harjot.crollerTest.Croller
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.NumberUtilities
import com.sildian.apps.togetrail.trail.model.Trail
import com.sildian.apps.togetrail.trail.model.TrailLevel
import com.sildian.apps.togetrail.trail.model.TrailType
import kotlinx.android.synthetic.main.fragment_trail_info_edit.view.*

/*************************************************************************************************
 * Allows to edit information about a trail
 * This fragment should be used as a nested fragment within a side sheet
 * @param trail : the related trail
 ************************************************************************************************/

class TrailInfoEditFragment(val trail: Trail?=null) :
    Fragment(),
    Croller.onProgressChangedListener {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        const val TAG_FRAGMENT="TAG_FRAGMENT"

        /**Values to set with the croller**/
        private const val VALUE_DURATION=1
        private const val VALUE_ASCENT=2
        private const val VALUE_DESCENT=3
        private const val VALUE_DISTANCE=4
        private const val VALUE_MAX_ELEVATION=5
        private const val VALUE_MIN_ELEVATION=6

        /**Values max**/
        private const val VALUE_MAXIMUM_DURATION=1440       //Max value for a duration (in minutes)
        private const val VALUE_MAXIMUM_DISTANCE=50000      //Max value for a distance (in meters)
        private const val VALUE_MAXIMUM_ALTITUDE=10000      //Max value for an altitude (in meters)
    }

    /***************************************Data*************************************************/

    private var currentValueToSet= VALUE_DURATION       //The current value to set with the croller

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val nameTextField by lazy {layout.fragment_trail_info_edit_text_field_name}
    private val levelTextFieldDropDown by lazy {layout.fragment_trail_info_edit_text_field_dropdown_level}
    private val typeTextFieldDropDown by lazy {layout.fragment_trail_info_edit_text_field_dropdown_type}
    private val valuesCroller by lazy {layout.fragment_trail_info_edit_croller_values}
    private val durationText by lazy {layout.fragment_trail_info_edit_text_duration}
    private val ascentText by lazy {layout.fragment_trail_info_edit_text_ascent}
    private val descentText by lazy {layout.fragment_trail_info_edit_text_descent}
    private val distanceText by lazy {layout.fragment_trail_info_edit_text_distance}
    private val maxElevationText by lazy {layout.fragment_trail_info_edit_text_max_elevation}
    private val minElevationText by lazy {layout.fragment_trail_info_edit_text_min_elevation}
    private val descriptionTextField by lazy {layout.fragment_trail_info_edit_text_field_description}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_trail_info_edit, container, false)
        initializeAllUIComponents()
        return this.layout
    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeNameTextField()
        initializeLevelTextFieldDropDown()
        initializeTypeTextFieldDropDown()
        initializeValuesCroller()
        initializeDurationText()
        initializeAscentText()
        initializeDescentText()
        initializeDistanceText()
        initializeMaxElevationText()
        initializeMinElevationText()
        initializeDescriptionTextField()
    }

    private fun initializeNameTextField(){
        this.nameTextField.setText(this.trail?.name)
    }

    private fun initializeLevelTextFieldDropDown(){
        val choice=resources.getStringArray(R.array.array_trail_levels)
        val adapter=ArrayAdapter<String>(context!!, R.layout.item_dropdown_menu, choice)
        this.levelTextFieldDropDown.setAdapter(adapter)
        val currentValue=this.trail?.level?.value?:TrailLevel.MEDIUM.value
        val currentText=this.levelTextFieldDropDown.adapter.getItem(currentValue-1)
        this.levelTextFieldDropDown.setText(currentText.toString(), false)
        this.levelTextFieldDropDown.tag=currentValue
        this.levelTextFieldDropDown.setOnItemClickListener { adapterView, view, position, id ->
            adapterView.tag=position+1
        }
    }

    private fun initializeTypeTextFieldDropDown(){
        val choice=resources.getStringArray(R.array.array_trail_types)
        val adapter=ArrayAdapter<String>(context!!, R.layout.item_dropdown_menu, choice)
        this.typeTextFieldDropDown.setAdapter(adapter)
        val currentValue=this.trail?.type?.value?: TrailType.HIKING.value
        val currentText=this.typeTextFieldDropDown.adapter.getItem(currentValue-1)
        this.typeTextFieldDropDown.setText(currentText.toString(), false)
        this.typeTextFieldDropDown.tag=currentValue
        this.typeTextFieldDropDown.adapter.getItem(currentValue-1)
        this.typeTextFieldDropDown.setOnItemClickListener { adapterView, view, position, id ->
            adapterView.tag=position+1
        }
    }

    private fun initializeValuesCroller(){
        val duration=this.trail?.duration
        updateCroller(VALUE_DURATION, duration)
        this.valuesCroller.setOnProgressChangedListener(this)
    }

    private fun initializeDurationText(){
        val duration=this.trail?.duration
        updateDuration(duration, false)
        this.durationText.setOnClickListener {
            val duration=this.trail?.duration
            updateCroller(VALUE_DURATION, duration)
        }
    }

    private fun initializeAscentText(){
        val ascent=this.trail?.ascent
        updateAscent(ascent, false)
        this.ascentText.setOnClickListener {
            val ascent=this.trail?.ascent
            updateCroller(VALUE_ASCENT, ascent)
        }
    }

    private fun initializeDescentText(){
        val descent=this.trail?.descent
        updateDescent(descent, false)
        this.descentText.setOnClickListener {
            val descent=this.trail?.descent
            updateCroller(VALUE_DESCENT, descent)
        }
    }

    private fun initializeDistanceText(){
        val distance=this.trail?.distance
        updateDistance(distance?:0, false)
        this.distanceText.setOnClickListener {
            val distance=this.trail?.distance
            updateCroller(VALUE_DISTANCE, distance)
        }
    }

    private fun initializeMaxElevationText(){
        val maxElevation=this.trail?.maxElevation
        updateMaxElevation(maxElevation, false)
        this.maxElevationText.setOnClickListener {
            val maxElevation=this.trail?.maxElevation
            updateCroller(VALUE_MAX_ELEVATION, maxElevation)
        }
    }

    private fun initializeMinElevationText(){
        val minElevation=this.trail?.minElevation
        updateMinElevation(minElevation, false)
        this.minElevationText.setOnClickListener {
            val minElevation=this.trail?.minElevation
            updateCroller(VALUE_MIN_ELEVATION, minElevation)
        }
    }

    private fun initializeDescriptionTextField(){
        this.descriptionTextField.setText(this.trail?.description)
    }

    /*****************************Values monitoring with croller*********************************/

    override fun onProgressChanged(progress: Int) {
        when(this.currentValueToSet){
            VALUE_DURATION -> updateDuration(progress, true)
            VALUE_ASCENT -> updateAscent(progress, true)
            VALUE_DESCENT -> updateDescent(progress, true)
            VALUE_DISTANCE -> updateDistance(progress, true)
            VALUE_MAX_ELEVATION -> updateMaxElevation(progress, true)
            VALUE_MIN_ELEVATION -> updateMinElevation(progress, true)
        }
    }

    private fun updateCroller(valueToSet:Int, currentValue:Int?){
        this.currentValueToSet=valueToSet
        when(this.currentValueToSet){
            VALUE_DURATION -> this.valuesCroller.max= VALUE_MAXIMUM_DURATION
            VALUE_DISTANCE-> this.valuesCroller.max= VALUE_MAXIMUM_DISTANCE
            else -> this.valuesCroller.max= VALUE_MAXIMUM_ALTITUDE
        }
        this.valuesCroller.progress=currentValue?:0
    }

    private fun updateDuration(duration:Int?, updateCroller:Boolean){
        this.trail?.duration=duration
        val hourMetric=resources.getString(R.string.metric_hour)
        val minuteMetric=resources.getString(R.string.metric_minute)
        val unknownText=resources.getString(R.string.message_unknown_short)
        val durationToDisplay=
            if(duration!=null)
                DateUtilities.displayDuration(duration.toLong(), hourMetric, minuteMetric)
            else unknownText
        this.durationText.text=durationToDisplay
        if(updateCroller) {
            this.valuesCroller.label = durationToDisplay
        }
    }

    private fun updateAscent(ascent:Int?, updateCroller: Boolean){
        this.trail?.ascent=ascent
        val metric=resources.getString(R.string.metric_meter)
        val suffix=resources.getString(R.string.label_trail_ascent_short)
        val unknownText=resources.getString(R.string.message_unknown_short)
        val ascentToDisplay=
            if(ascent!=null)
                NumberUtilities.displayNumberWithMetricAndSuffix(
                    ascent.toDouble(), 0, metric, suffix, true)
            else unknownText
        this.ascentText.text=ascentToDisplay
        if(updateCroller) {
            val ascentCroller =
                NumberUtilities.displayNumberWithMetric(ascent?.toDouble()?:0.toDouble(), 0, metric)
            this.valuesCroller.label = ascentCroller
        }
    }

    private fun updateDescent(descent:Int?, updateCroller: Boolean){
        this.trail?.descent=descent
        val metric=resources.getString(R.string.metric_meter)
        val suffix=resources.getString(R.string.label_trail_descent_short)
        val unknownText=resources.getString(R.string.message_unknown_short)
        val descentToDisplay=
            if(descent!=null)
                NumberUtilities.displayNumberWithMetricAndSuffix(
                    descent.toDouble(), 0, metric, suffix, true)
            else unknownText
        this.descentText.text=descentToDisplay
        if(updateCroller) {
            val descentCroller =
                NumberUtilities.displayNumberWithMetric(descent?.toDouble()?:0.toDouble(), 0, metric)
            this.valuesCroller.label = descentCroller
        }
    }

    private fun updateDistance(distance:Int, updateCroller: Boolean){
        this.trail?.distance=distance
        val metric=resources.getString(R.string.metric_kilometer)
        val suffix=resources.getString(R.string.label_trail_distance_short)
        val distanceToDisplay=NumberUtilities.displayNumberWithMetricAndSuffix(
            distance.toDouble()/1000, 1, metric, suffix, true)
        this.distanceText.text=distanceToDisplay
        if(updateCroller) {
            val distanceCroller =
                NumberUtilities.displayNumberWithMetric(distance.toDouble() / 1000, 1, metric)
            this.valuesCroller.label = distanceCroller
        }
    }

    private fun updateMaxElevation(elevation:Int?, updateCroller: Boolean){
        this.trail?.maxElevation=elevation
        val metric=resources.getString(R.string.metric_meter)
        val suffix=resources.getString(R.string.label_trail_max_elevation_short)
        val unknownText=resources.getString(R.string.message_unknown_short)
        val elevationToDisplay=
            if(elevation!=null)
                NumberUtilities.displayNumberWithMetricAndSuffix(
                    elevation.toDouble(), 0, metric, suffix, true)
            else unknownText
        this.maxElevationText.text=elevationToDisplay
        if(updateCroller) {
            val elevationCroller =
                NumberUtilities.displayNumberWithMetric(elevation?.toDouble()?:0.toDouble(), 0, metric)
            this.valuesCroller.label = elevationCroller
        }
    }

    private fun updateMinElevation(elevation:Int?, updateCroller: Boolean){
        this.trail?.minElevation=elevation
        val metric=resources.getString(R.string.metric_meter)
        val suffix=resources.getString(R.string.label_trail_min_elevation_short)
        val unknownText=resources.getString(R.string.message_unknown_short)
        val elevationToDisplay=
            if(elevation!=null)
                NumberUtilities.displayNumberWithMetricAndSuffix(
                    elevation.toDouble(), 0, metric, suffix, true)
            else unknownText
        this.minElevationText.text=elevationToDisplay
        if(updateCroller) {
            val elevationCroller =
                NumberUtilities.displayNumberWithMetric(elevation?.toDouble()?:0.toDouble(), 0, metric)
            this.valuesCroller.label = elevationCroller
        }
    }
}
