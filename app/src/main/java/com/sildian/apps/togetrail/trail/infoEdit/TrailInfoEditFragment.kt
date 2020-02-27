package com.sildian.apps.togetrail.trail.infoEdit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sdsmdg.harjot.crollerTest.Croller
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.SaveDataFlow
import com.sildian.apps.togetrail.common.model.Location
import com.sildian.apps.togetrail.common.utils.uiHelpers.DropdownMenuHelper
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import com.sildian.apps.togetrail.trail.model.core.TrailType
import kotlinx.android.synthetic.main.fragment_trail_info_edit.view.*

/*************************************************************************************************
 * Allows to edit information about a trail
 * @param trail : the related trail
 ************************************************************************************************/

class TrailInfoEditFragment(val trail: Trail?=null) :
    Fragment(),
    SaveDataFlow,
    Croller.onProgressChangedListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG_FRAGMENT="TAG_FRAGMENT"

        /**Metrics to set with the croller**/
        private const val METRIC_DURATION=0
        private const val METRIC_ASCENT=1
        private const val METRIC_DESCENT=2
        private const val METRIC_DISTANCE=3
        private const val METRIC_MAX_ELEVATION=4
        private const val METRIC_MIN_ELEVATION=5

        /**Values max**/
        private const val VALUE_MAX_DURATION=720        //Max value for a duration (in minutes)
        private const val VALUE_MAX_DISTANCE=30000      //Max value for a distance (in meters)
        private const val VALUE_MAX_ALTITUDE=4000       //Max value for an altitude (in meters)
    }

    /***************************************Data*************************************************/

    private var currentMetricToSet= METRIC_DURATION     //The current metric to set with the croller

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val nameTextField by lazy {layout.fragment_trail_info_edit_text_field_name}
    private val levelTextFieldDropDown by lazy {layout.fragment_trail_info_edit_text_field_dropdown_level}
    private val typeTextFieldDropDown by lazy {layout.fragment_trail_info_edit_text_field_dropdown_type}
    private val metricsCroller by lazy {layout.fragment_trail_info_edit_croller_metrics}
    private val durationText by lazy {layout.fragment_trail_info_edit_text_duration}
    private val ascentText by lazy {layout.fragment_trail_info_edit_text_ascent}
    private val descentText by lazy {layout.fragment_trail_info_edit_text_descent}
    private val distanceText by lazy {layout.fragment_trail_info_edit_text_distance}
    private val maxElevationText by lazy {layout.fragment_trail_info_edit_text_max_elevation}
    private val minElevationText by lazy {layout.fragment_trail_info_edit_text_min_elevation}
    private val countryTextField by lazy {layout.fragment_trail_info_edit_text_field_country}
    private val regionTextField by lazy {layout.fragment_trail_info_edit_text_field_region}
    private val townTextField by lazy {layout.fragment_trail_info_edit_text_field_town}
    private val descriptionTextField by lazy {layout.fragment_trail_info_edit_text_field_description}

    private val metricsTexts by lazy {
        arrayOf(
            this.durationText,
            this.ascentText,
            this.descentText,
            this.distanceText,
            this.maxElevationText,
            this.minElevationText
        )
    }

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_trail_info_edit, container, false)
        initializeAllUIComponents()
        return this.layout
    }

    /*********************************Data monitoring********************************************/

    override fun saveData() {
        this.trail?.name=this.nameTextField.text.toString()
        this.trail?.level=
            TrailLevel.fromValue(this.levelTextFieldDropDown.tag.toString().toInt()+1)
        this.trail?.type=
            TrailType.fromValue(this.typeTextFieldDropDown.tag.toString().toInt()+1)
        this.trail?.location = Location(
            this.countryTextField.text.toString(),
            this.regionTextField.text.toString(),
            this.townTextField.text.toString()
        )
        this.trail?.description=this.descriptionTextField.text.toString()
        (activity as TrailInfoEditActivity).updateTrailAndSave(this.trail!!)
    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeNameTextField()
        initializeLevelTextFieldDropDown()
        initializeTypeTextFieldDropDown()
        initializeMetricsCroller()
        initializeDurationText()
        initializeAscentText()
        initializeDescentText()
        initializeDistanceText()
        initializeMaxElevationText()
        initializeMinElevationText()
        initializeCountryTextField()
        initializeRegionTextField()
        initializeTownTextField()
        initializeDescriptionTextField()
        updateCurrentMetricToSet(METRIC_DURATION)
    }

    private fun initializeNameTextField(){
        this.nameTextField.setText(this.trail?.name)
    }

    private fun initializeLevelTextFieldDropDown(){
        val choice=resources.getStringArray(R.array.array_trail_levels)
        val initialValue=(this.trail?.level?.value?: TrailLevel.MEDIUM.value)-1
        DropdownMenuHelper.populateDropdownMenu(this.levelTextFieldDropDown, choice, initialValue)
    }

    private fun initializeTypeTextFieldDropDown(){
        val choice=resources.getStringArray(R.array.array_trail_types)
        val initialValue=(this.trail?.type?.value?: TrailType.HIKING.value)-1
        DropdownMenuHelper.populateDropdownMenu(this.typeTextFieldDropDown, choice, initialValue)
    }

    private fun initializeMetricsCroller(){
        val duration=this.trail?.duration
        updateCroller(METRIC_DURATION, duration)
        this.metricsCroller.setOnProgressChangedListener(this)
    }

    private fun initializeDurationText(){
        val durationToDisplay=MetricsHelper.displayDuration(context!!, this.trail?.duration?.toLong())
        this.durationText.text=durationToDisplay
        this.durationText.setOnClickListener {
            val duration=this.trail?.duration
            updateCurrentMetricToSet(METRIC_DURATION)
            updateCroller(METRIC_DURATION, duration)
        }
    }

    private fun initializeAscentText(){
        val ascentToDisplay=MetricsHelper.displayAscent(
            context!!, this.trail?.ascent, true, true)
        this.ascentText.text=ascentToDisplay
        this.ascentText.setOnClickListener {
            val ascent=this.trail?.ascent
            updateCurrentMetricToSet(METRIC_ASCENT)
            updateCroller(METRIC_ASCENT, ascent)
        }
    }

    private fun initializeDescentText(){
        val descentToDisplay=MetricsHelper.displayDescent(
            context!!, this.trail?.descent, true, true)
        this.descentText.text=descentToDisplay
        this.descentText.setOnClickListener {
            val descent=this.trail?.descent
            updateCurrentMetricToSet(METRIC_DESCENT)
            updateCroller(METRIC_DESCENT, descent)
        }
    }

    private fun initializeDistanceText(){
        val distanceToDisplay=MetricsHelper.displayDistance(
            context!!, this.trail?.distance, true, true)
        this.distanceText.text=distanceToDisplay
        this.distanceText.setOnClickListener {
            val distance=this.trail?.distance
            updateCurrentMetricToSet(METRIC_DISTANCE)
            updateCroller(METRIC_DISTANCE, distance)
        }
    }

    private fun initializeMaxElevationText(){
        val maxElevationToDisplay=MetricsHelper.displayMaxElevation(
            context!!, this.trail?.maxElevation, true, true)
        this.maxElevationText.text=maxElevationToDisplay
        this.maxElevationText.setOnClickListener {
            val maxElevation=this.trail?.maxElevation
            updateCurrentMetricToSet(METRIC_MAX_ELEVATION)
            updateCroller(METRIC_MAX_ELEVATION, maxElevation)
        }
    }

    private fun initializeMinElevationText(){
        val minElevationToDisplay=MetricsHelper.displayMinElevation(
            context!!, this.trail?.minElevation, true, true)
        this.minElevationText.text=minElevationToDisplay
        this.minElevationText.setOnClickListener {
            val minElevation=this.trail?.minElevation
            updateCurrentMetricToSet(METRIC_MIN_ELEVATION)
            updateCroller(METRIC_MIN_ELEVATION, minElevation)
        }
    }

    private fun initializeCountryTextField(){
        this.countryTextField.setText(this.trail?.location?.country)
    }

    private fun initializeRegionTextField(){
        this.regionTextField.setText(this.trail?.location?.region)
    }

    private fun initializeTownTextField(){
        this.townTextField.setText(this.trail?.location?.town)
    }

    private fun initializeDescriptionTextField(){
        this.descriptionTextField.setText(this.trail?.description)
    }

    private fun updateCurrentMetricToSet(metricToSet: Int){
        this.currentMetricToSet=metricToSet
        for(i in this.metricsTexts.indices){
            if(i==this.currentMetricToSet){
                val color = ContextCompat.getColor(context!!, R.color.colorSecondaryDark)
                this.metricsTexts[i].setTextColor(color)
            }
            else{
                val color = ContextCompat.getColor(context!!, R.color.colorBlack)
                this.metricsTexts[i].setTextColor(color)
            }
        }
    }

    /*****************************Metrics monitoring with croller*********************************/

    override fun onProgressChanged(progress: Int) {
        updateCurrentMetric(progress)
    }

    private fun updateCroller(valueToSet:Int, currentValue:Int?){
        this.currentMetricToSet=valueToSet
        when(this.currentMetricToSet){
            METRIC_DURATION -> this.metricsCroller.max= VALUE_MAX_DURATION
            METRIC_DISTANCE -> this.metricsCroller.max= VALUE_MAX_DISTANCE
            else -> this.metricsCroller.max= VALUE_MAX_ALTITUDE
        }
        this.metricsCroller.progress=currentValue?:0
    }

    private fun updateCurrentMetric(value:Int?){
        when(this.currentMetricToSet){
            METRIC_DURATION -> updateDuration(value?:this.trail?.duration)
            METRIC_ASCENT -> updateAscent(value?:this.trail?.ascent)
            METRIC_DESCENT -> updateDescent(value?:this.trail?.descent)
            METRIC_DISTANCE -> updateDistance(value?:this.trail?.distance?:0)
            METRIC_MAX_ELEVATION -> updateMaxElevation(value?:this.trail?.maxElevation)
            METRIC_MIN_ELEVATION -> updateMinElevation(value?:this.trail?.minElevation)
        }
    }

    private fun updateDuration(duration:Int?){
        this.trail?.duration=duration
        val durationToDisplay=MetricsHelper.displayDuration(context!!, duration?.toLong())
        this.durationText.text=durationToDisplay
        this.metricsCroller.label = durationToDisplay
    }

    private fun updateAscent(ascent:Int?) {
        this.trail?.ascent = ascent
        val ascentToDisplay = MetricsHelper.displayAscent(
            context!!, ascent, true, true)
        val ascentToDisplayInCroller = MetricsHelper.displayAscent(
            context!!, ascent, false, false)
        this.ascentText.text = ascentToDisplay
        this.metricsCroller.label = ascentToDisplayInCroller
    }

    private fun updateDescent(descent:Int?) {
        this.trail?.descent = descent
        val descentToDisplay = MetricsHelper.displayDescent(
            context!!, descent, true, true)
        val descentToDisplayInCroller = MetricsHelper.displayAscent(
            context!!, descent, false, false)
        this.descentText.text = descentToDisplay
        this.metricsCroller.label = descentToDisplayInCroller
    }

    private fun updateDistance(distance:Int) {
        this.trail?.distance = distance
        val distanceToDisplay = MetricsHelper.displayDistance(
            context!!, distance, true, true)
        val distanceToDisplayInCroller = MetricsHelper.displayAscent(
            context!!, distance, false, false)
        this.distanceText.text = distanceToDisplay
        this.metricsCroller.label = distanceToDisplayInCroller
    }

    private fun updateMaxElevation(elevation:Int?) {
        this.trail?.maxElevation = elevation
        val maxElevationToDisplay = MetricsHelper.displayMaxElevation(
            context!!, elevation, true, true)
        val maxElevationToDisplayInCroller = MetricsHelper.displayMaxElevation(
            context!!, elevation, false, false)
        this.maxElevationText.text = maxElevationToDisplay
        this.metricsCroller.label = maxElevationToDisplayInCroller
    }

    private fun updateMinElevation(elevation:Int?) {
        this.trail?.minElevation = elevation
        val minElevationToDisplay = MetricsHelper.displayMinElevation(
            context!!, elevation, true, true)
        val minElevationToDisplayInCroller = MetricsHelper.displayMinElevation(
            context!!, elevation, false, false)
        this.minElevationText.text = minElevationToDisplay
        this.metricsCroller.label = minElevationToDisplayInCroller
    }
}
