package com.sildian.apps.togetrail.trail.infoEdit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.sdsmdg.harjot.crollerTest.Croller
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.model.Location
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.NumberUtilities
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import com.sildian.apps.togetrail.trail.model.core.TrailType
import kotlinx.android.synthetic.main.fragment_trail_info_edit.view.*

/*************************************************************************************************
 * Allows to edit information about a trail
 * @param trail : the related trail
 ************************************************************************************************/

class TrailInfoEditFragment(val trail: Trail?=null) :
    BaseTrailInfoEditFragment(),
    Croller.onProgressChangedListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        const val TAG_FRAGMENT="TAG_FRAGMENT"

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
            TrailLevel.fromValue(this.levelTextFieldDropDown.tag.toString().toInt())
        this.trail?.type= TrailType.fromValue(this.typeTextFieldDropDown.tag.toString().toInt())
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
        val adapter=ArrayAdapter<String>(context!!, R.layout.item_dropdown_menu, choice)
        this.levelTextFieldDropDown.setAdapter(adapter)
        val currentValue=this.trail?.level?.value?: TrailLevel.MEDIUM.value
        val currentText=this.levelTextFieldDropDown.adapter.getItem(currentValue-1)
        this.levelTextFieldDropDown.setText(currentText.toString(), false)
        this.levelTextFieldDropDown.tag=currentValue
        this.levelTextFieldDropDown.setOnItemClickListener { adapterView, view, position, id ->
            this.levelTextFieldDropDown.tag=position+1
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
            this.typeTextFieldDropDown.tag=position+1
        }
    }

    private fun initializeMetricsCroller(){
        val duration=this.trail?.duration
        updateCroller(METRIC_DURATION, duration)
        this.metricsCroller.setOnProgressChangedListener(this)
    }

    private fun initializeDurationText(){
        val duration=this.trail?.duration
        updateDuration(duration, false)
        this.durationText.setOnClickListener {
            val duration=this.trail?.duration
            updateCurrentMetricToSet(METRIC_DURATION)
            updateCroller(METRIC_DURATION, duration)
        }
    }

    private fun initializeAscentText(){
        val ascent=this.trail?.ascent
        updateAscent(ascent, false)
        this.ascentText.setOnClickListener {
            val ascent=this.trail?.ascent
            updateCurrentMetricToSet(METRIC_ASCENT)
            updateCroller(METRIC_ASCENT, ascent)
        }
    }

    private fun initializeDescentText(){
        val descent=this.trail?.descent
        updateDescent(descent, false)
        this.descentText.setOnClickListener {
            val descent=this.trail?.descent
            updateCurrentMetricToSet(METRIC_DESCENT)
            updateCroller(METRIC_DESCENT, descent)
        }
    }

    private fun initializeDistanceText(){
        val distance=this.trail?.distance
        updateDistance(distance?:0, false)
        this.distanceText.setOnClickListener {
            val distance=this.trail?.distance
            updateCurrentMetricToSet(METRIC_DISTANCE)
            updateCroller(METRIC_DISTANCE, distance)
        }
    }

    private fun initializeMaxElevationText(){
        val maxElevation=this.trail?.maxElevation
        updateMaxElevation(maxElevation, false)
        this.maxElevationText.setOnClickListener {
            val maxElevation=this.trail?.maxElevation
            updateCurrentMetricToSet(METRIC_MAX_ELEVATION)
            updateCroller(METRIC_MAX_ELEVATION, maxElevation)
        }
    }

    private fun initializeMinElevationText(){
        val minElevation=this.trail?.minElevation
        updateMinElevation(minElevation, false)
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
            METRIC_DURATION -> this.metricsCroller.max=
                VALUE_MAX_DURATION
            METRIC_DISTANCE -> this.metricsCroller.max=
                VALUE_MAX_DISTANCE
            else -> this.metricsCroller.max=
                VALUE_MAX_ALTITUDE
        }
        this.metricsCroller.progress=currentValue?:0
    }

    private fun updateCurrentMetric(value:Int?){
        when(this.currentMetricToSet){
            METRIC_DURATION -> updateDuration(value?:this.trail?.duration, true)
            METRIC_ASCENT -> updateAscent(value?:this.trail?.ascent, true)
            METRIC_DESCENT -> updateDescent(value?:this.trail?.descent, true)
            METRIC_DISTANCE -> updateDistance(value?:this.trail?.distance?:0, true)
            METRIC_MAX_ELEVATION -> updateMaxElevation(value?:this.trail?.maxElevation, true)
            METRIC_MIN_ELEVATION -> updateMinElevation(value?:this.trail?.minElevation, true)
        }
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
            this.metricsCroller.label = durationToDisplay
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
            this.metricsCroller.label = ascentCroller
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
            this.metricsCroller.label = descentCroller
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
            this.metricsCroller.label = distanceCroller
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
            this.metricsCroller.label = elevationCroller
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
            this.metricsCroller.label = elevationCroller
        }
    }
}
