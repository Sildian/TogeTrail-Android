package com.sildian.apps.togetrail.trail.infoEdit

import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.uiHelpers.DropdownMenuHelper
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.common.views.circularSlider.CircularSlider
import com.sildian.apps.togetrail.common.views.circularSlider.ValueFormaters
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import kotlinx.android.synthetic.main.fragment_trail_info_edit.view.*

/*************************************************************************************************
 * Allows to edit information about a trail
 * @param trail : the related trail
 ************************************************************************************************/

class TrailInfoEditFragment(private val trail: Trail?=null) :
    BaseImagePickerFragment(),
    CircularSlider.OnValueChangedListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Metrics to set with the slider**/
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

    private var currentMetricToSet= METRIC_DURATION     //The current metric to set with the slider

    /**********************************UI component**********************************************/

    private val nameTextFieldLayout by lazy {layout.fragment_trail_info_edit_text_field_layout_name}
    private val nameTextField by lazy {layout.fragment_trail_info_edit_text_field_name}
    private val levelTextFieldDropDownLayout by lazy {layout.fragment_trail_info_edit_text_field_dropdown_layout_level}
    private val levelTextFieldDropDown by lazy {layout.fragment_trail_info_edit_text_field_dropdown_level}
    private val loopSwitch by lazy {layout.fragment_trail_info_edit_switch_loop}
    private val photoText by lazy {layout.fragment_trail_info_edit_text_photo}
    private val photoImageView by lazy {layout.fragment_trail_info_edit_image_view_photo}
    private val deletePhotoButton by lazy {layout.fragment_trail_info_edit_button_delete_photo}
    private val addPhotoButton by lazy {layout.fragment_trail_info_edit_button_add_photo}
    private val metricsSlider by lazy {layout.fragment_trail_info_edit_slider_metrics}
    private val durationText by lazy {layout.fragment_trail_info_edit_text_duration}
    private val ascentText by lazy {layout.fragment_trail_info_edit_text_ascent}
    private val descentText by lazy {layout.fragment_trail_info_edit_text_descent}
    private val distanceText by lazy {layout.fragment_trail_info_edit_text_distance}
    private val maxElevationText by lazy {layout.fragment_trail_info_edit_text_max_elevation}
    private val minElevationText by lazy {layout.fragment_trail_info_edit_text_min_elevation}
    private val resetMetricsButton by lazy {layout.fragment_trail_info_edit_button_metrics_reset}
    private val locationTextFieldLayout by lazy {layout.fragment_trail_info_edit_text_field_layout_location}
    private val locationTextField by lazy {layout.fragment_trail_info_edit_text_field_location}
    private val descriptionTextField by lazy {layout.fragment_trail_info_edit_text_field_description}
    private val selectPhotoButton by lazy {layout.fragment_trail_info_edit_button_select_photo}
    private val takePhotoButton by lazy {layout.fragment_trail_info_edit_button_take_photo}
    private val messageView by lazy {layout.fragment_trail_info_edit_view_message}
    private val messageAnchorView by lazy {layout.fragment_trail_info_edit_bottom_sheet_add_photo}

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

    /*********************************Data monitoring********************************************/

    override fun updateData(data:Any?) {
        if(data is Location){
            this.trail?.location=data
            updateLocationTextField()
        }
    }

    override fun saveData() {
        if(checkDataIsValid()) {
            this.trail?.name = this.nameTextField.text.toString()
            this.trail?.level =
                TrailLevel.fromValue(this.levelTextFieldDropDown.tag.toString().toInt())
            this.trail?.loop=this.loopSwitch.isChecked
            this.trail?.description = this.descriptionTextField.text.toString()
            this.trail?.autoPopulatePosition()
            (activity as TrailInfoEditActivity).saveTrail()
        }
    }

    override fun checkDataIsValid(): Boolean {
        if(checkTextFieldsAreNotEmpty()){
            if(checkTextFieldsDropDownAreNotUnknown()){
                return true
            }else{
                Snackbar.make(this.messageView, R.string.message_trail_level_unknown, Snackbar.LENGTH_LONG)
                    .setAnchorView(this.messageAnchorView)
                    .show()
            }
        }else{
            Snackbar.make(this.messageView, R.string.message_text_fields_empty, Snackbar.LENGTH_LONG)
                .setAnchorView(this.messageAnchorView)
                .show()
        }
        return false
    }

    private fun checkTextFieldsAreNotEmpty():Boolean{
        val textFieldsAndLayouts:HashMap<TextInputEditText, TextInputLayout> =hashMapOf()
        textFieldsAndLayouts[this.nameTextField]=this.nameTextFieldLayout
        textFieldsAndLayouts[this.locationTextField]=this.locationTextFieldLayout
        return TextFieldHelper.checkAllTextFieldsAreNotEmpty(textFieldsAndLayouts)
    }

    private fun checkTextFieldsDropDownAreNotUnknown():Boolean{
        return if(this.levelTextFieldDropDown.tag==0){
            this.levelTextFieldDropDownLayout.error=getString(R.string.message_text_field_empty)
            false
        }else{
            this.levelTextFieldDropDownLayout.error=null
            true
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_info_edit

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_trail_info_edit_bottom_sheet_add_photo

    override fun initializeUI() {
        initializeLoopSwitch()
        initializeDeletePhotoButton()
        initializeAddPhotoButton()
        initializeMetricsSlider()
        initializeDurationText()
        initializeAscentText()
        initializeDescentText()
        initializeDistanceText()
        initializeMaxElevationText()
        initializeMinElevationText()
        initializeResetMetricsButton()
        initializeSelectPhotoButton()
        initializeTakePhotoButton()
        refreshUI()
    }

    override fun refreshUI() {
        updateNameTextField()
        updateLevelTextFieldDropDown()
        updateCurrentMetricToSet(METRIC_DURATION)
        updateLocationTextField()
        updateDescriptionTextField()
        updatePhoto()
    }

    private fun updateNameTextField(){
        this.nameTextField.setText(this.trail?.name)
    }

    private fun updateLevelTextFieldDropDown(){
        val choice=resources.getStringArray(R.array.array_trail_levels)
        val initialValue=(this.trail?.level?.value?: TrailLevel.MEDIUM.value)
        DropdownMenuHelper.populateDropdownMenu(this.levelTextFieldDropDown, choice, initialValue)
    }

    private fun initializeLoopSwitch(){
        this.trail?.let { trail ->
            this.loopSwitch.isChecked = trail.loop
        }
    }

    private fun initializeDeletePhotoButton(){
        this.deletePhotoButton.setOnClickListener {
            deletePhoto()
        }
    }

    private fun initializeAddPhotoButton(){
        this.addPhotoButton.setOnClickListener {
            expandAddPhotoBottomSheet()
        }
    }

    private fun initializeMetricsSlider(){
        val duration=this.trail?.duration
        updateMetricsSlider(METRIC_DURATION, duration)
        this.metricsSlider.addOnValueChangedListener(this)
    }

    private fun initializeDurationText(){
        val durationToDisplay=MetricsHelper.displayDuration(context!!, this.trail?.duration?.toLong())
        this.durationText.text=durationToDisplay
        this.durationText.setOnClickListener {
            val duration=this.trail?.duration
            updateCurrentMetricToSet(METRIC_DURATION)
            updateMetricsSlider(METRIC_DURATION, duration)
        }
    }

    private fun initializeAscentText(){
        val ascentToDisplay=MetricsHelper.displayAscent(
            context!!, this.trail?.ascent, true, true)
        this.ascentText.text=ascentToDisplay
        this.ascentText.setOnClickListener {
            val ascent=this.trail?.ascent
            updateCurrentMetricToSet(METRIC_ASCENT)
            updateMetricsSlider(METRIC_ASCENT, ascent)
        }
    }

    private fun initializeDescentText(){
        val descentToDisplay=MetricsHelper.displayDescent(
            context!!, this.trail?.descent, true, true)
        this.descentText.text=descentToDisplay
        this.descentText.setOnClickListener {
            val descent=this.trail?.descent
            updateCurrentMetricToSet(METRIC_DESCENT)
            updateMetricsSlider(METRIC_DESCENT, descent)
        }
    }

    private fun initializeDistanceText(){
        val distanceToDisplay=MetricsHelper.displayDistance(
            context!!, this.trail?.distance, true, true)
        this.distanceText.text=distanceToDisplay
        this.distanceText.setOnClickListener {
            val distance=this.trail?.distance
            updateCurrentMetricToSet(METRIC_DISTANCE)
            updateMetricsSlider(METRIC_DISTANCE, distance)
        }
    }

    private fun initializeMaxElevationText(){
        val maxElevationToDisplay=MetricsHelper.displayMaxElevation(
            context!!, this.trail?.maxElevation, true, true)
        this.maxElevationText.text=maxElevationToDisplay
        this.maxElevationText.setOnClickListener {
            val maxElevation=this.trail?.maxElevation
            updateCurrentMetricToSet(METRIC_MAX_ELEVATION)
            updateMetricsSlider(METRIC_MAX_ELEVATION, maxElevation)
        }
    }

    private fun initializeMinElevationText(){
        val minElevationToDisplay=MetricsHelper.displayMinElevation(
            context!!, this.trail?.minElevation, true, true)
        this.minElevationText.text=minElevationToDisplay
        this.minElevationText.setOnClickListener {
            val minElevation=this.trail?.minElevation
            updateCurrentMetricToSet(METRIC_MIN_ELEVATION)
            updateMetricsSlider(METRIC_MIN_ELEVATION, minElevation)
        }
    }

    private fun initializeResetMetricsButton(){
        this.resetMetricsButton.setOnClickListener {
            val value=when(this.currentMetricToSet){
                METRIC_DURATION -> {
                    this.trail?.autoCalculateDuration()
                    this.trail?.duration
                }
                METRIC_ASCENT -> {
                    this.trail?.autoCalculateAscent()
                    this.trail?.ascent
                }
                METRIC_DESCENT -> {
                    this.trail?.autoCalculateDescent()
                    this.trail?.descent
                }
                METRIC_DISTANCE -> {
                    this.trail?.autoCalculateDistance()
                    this.trail?.distance
                }
                METRIC_MAX_ELEVATION -> {
                    this.trail?.autoCalculateMaxElevation()
                    this.trail?.maxElevation
                }
                METRIC_MIN_ELEVATION -> {
                    this.trail?.autoCalculateMinElevation()
                    this.trail?.minElevation
                }
                else -> null
            }
            updateMetricsSlider(this.currentMetricToSet, value)
            updateCurrentMetric(value)
        }
    }

    private fun updateLocationTextField(){
        this.locationTextField.setText(this.trail?.location?.fullAddress)
        this.locationTextField.setOnClickListener {
            (activity as TrailInfoEditActivity).searchLocation()
        }
    }

    private fun updateDescriptionTextField(){
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

    private fun initializeSelectPhotoButton(){
        this.selectPhotoButton.setOnClickListener {
            requestWritePermission()
        }
    }

    private fun initializeTakePhotoButton(){
        this.takePhotoButton.setOnClickListener {
            requestWriteAndCameraPermission()
        }
    }

    private fun updatePhoto(){
        Glide.with(context!!)
            .load(this.trail?.mainPhotoUrl)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_trail_black)
            .into(this.photoImageView)
        updatePhotoVisibility()
    }

    private fun updatePhotoVisibility(){

        /*If no photo is available, shows a text to notify the user*/

        if(this.trail?.mainPhotoUrl.isNullOrEmpty()){
            this.photoText.visibility=View.VISIBLE
            this.photoImageView.visibility=View.INVISIBLE
            this.deletePhotoButton.visibility=View.INVISIBLE
        }

        /*Else shows the image*/

        else{
            this.photoText.visibility=View.INVISIBLE
            this.photoImageView.visibility=View.VISIBLE
            this.deletePhotoButton.visibility=View.VISIBLE
        }
    }

    /*********************************Metrics monitoring*****************************************/

    override fun onValueChanged(view: CircularSlider, value: Int) {
        updateCurrentMetric(value)
    }

    private fun updateMetricsSlider(valueToSet:Int, currentValue:Int?){
        this.currentMetricToSet=valueToSet
        when(this.currentMetricToSet){
            METRIC_DURATION -> {
                this.metricsSlider.valueFormater=ValueFormaters.DurationValueFormater()
                this.metricsSlider.setMaxValue(VALUE_MAX_DURATION)
            }
            METRIC_DISTANCE -> {
                this.metricsSlider.valueFormater=ValueFormaters.DistanceValueFormater()
                this.metricsSlider.setMaxValue(VALUE_MAX_DISTANCE)
            }
            else -> {
                this.metricsSlider.valueFormater=ValueFormaters.AltitudeValueFormater()
                this.metricsSlider.setMaxValue(VALUE_MAX_ALTITUDE)
            }
        }
        this.metricsSlider.setCurrentValue(currentValue?:0)
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
    }

    private fun updateAscent(ascent:Int?) {
        this.trail?.ascent = ascent
        val ascentToDisplay = MetricsHelper.displayAscent(
            context!!, ascent, true, true)
        this.ascentText.text = ascentToDisplay
    }

    private fun updateDescent(descent:Int?) {
        this.trail?.descent = descent
        val descentToDisplay = MetricsHelper.displayDescent(
            context!!, descent, true, true)
        this.descentText.text = descentToDisplay
    }

    private fun updateDistance(distance:Int) {
        this.trail?.distance = distance
        val distanceToDisplay = MetricsHelper.displayDistance(
            context!!, distance, true, true)
        this.distanceText.text = distanceToDisplay
    }

    private fun updateMaxElevation(elevation:Int?) {
        this.trail?.maxElevation = elevation
        val maxElevationToDisplay = MetricsHelper.displayMaxElevation(
            context!!, elevation, true, true)
        this.maxElevationText.text = maxElevationToDisplay
    }

    private fun updateMinElevation(elevation:Int?) {
        this.trail?.minElevation = elevation
        val minElevationToDisplay = MetricsHelper.displayMinElevation(
            context!!, elevation, true, true)
        this.minElevationText.text = minElevationToDisplay
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String){
        (activity as TrailInfoEditActivity)
            .updateImagePathToUploadIntoDatabase(filePath)
        this.trail?.mainPhotoUrl=filePath
        updatePhoto()
    }

    override fun deletePhoto(){
        if(!this.trail?.mainPhotoUrl.isNullOrEmpty()) {
            (activity as TrailInfoEditActivity)
                .updateImagePathToDeleteFromDatabase(this.trail?.mainPhotoUrl!!)
            this.trail.mainPhotoUrl = null
            updatePhoto()
        }
    }
}
