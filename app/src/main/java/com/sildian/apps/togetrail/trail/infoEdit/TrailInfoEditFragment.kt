package com.sildian.apps.togetrail.trail.infoEdit

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
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
import com.sildian.apps.togetrail.databinding.FragmentTrailInfoEditBinding
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import kotlinx.android.synthetic.main.fragment_trail_info_edit.view.*

/*************************************************************************************************
 * Allows to edit information about a trail
 * @param trailViewModel : the trail data
 ************************************************************************************************/

class TrailInfoEditFragment(private val trailViewModel: TrailViewModel?=null) :
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
    private val photoImageView by lazy {layout.fragment_trail_info_edit_image_view_photo}
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

    override fun loadData() {
        (this.binding as FragmentTrailInfoEditBinding).trailInfoEditFragment=this
        (this.binding as FragmentTrailInfoEditBinding).trailViewModel=this.trailViewModel
        this.trailViewModel?.addOnPropertyChangedCallback(object:Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                refreshUI()
            }
        })
    }

    override fun updateData(data:Any?) {
        if(data is Location){
            this.trailViewModel?.trail?.location=data
            this.locationTextField.setText(data.fullAddress)
        }
    }

    override fun saveData() {
        if(checkDataIsValid()) {
            this.trailViewModel?.trail?.name = this.nameTextField.text.toString()
            this.trailViewModel?.trail?.level =
                TrailLevel.fromValue(this.levelTextFieldDropDown.tag.toString().toInt())
            this.trailViewModel?.trail?.loop=this.loopSwitch.isChecked
            this.trailViewModel?.trail?.description = this.descriptionTextField.text.toString()
            this.trailViewModel?.trail?.autoPopulatePosition()
            this.baseActivity?.showProgressDialog()
            this.trailViewModel?.saveTrailInDatabase(false, this::handleSaveDataSuccess, this::handleQueryError)
        }
    }

    private fun handleSaveDataSuccess(){
        this.baseActivity?.dismissProgressDialog()
        (activity as TrailInfoEditActivity).finishOk()
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

    override fun useDataBinding(): Boolean = true

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_trail_info_edit_bottom_sheet_add_photo

    override fun initializeUI() {
        initializeMetricsSlider()
        initializeDurationText()
        initializeAscentText()
        initializeDescentText()
        initializeDistanceText()
        initializeMaxElevationText()
        initializeMinElevationText()
        initializeResetMetricsButton()
        refreshUI()
    }

    override fun refreshUI() {
        updateLevelTextFieldDropDown()
        updateCurrentMetricToSet(METRIC_DURATION)
        updatePhoto()
    }

    private fun updateLevelTextFieldDropDown(){
        val choice=resources.getStringArray(R.array.array_trail_levels)
        val initialValue=(this.trailViewModel?.trail?.level?.value?: TrailLevel.MEDIUM.value)
        DropdownMenuHelper.populateDropdownMenu(this.levelTextFieldDropDown, choice, initialValue)
    }

    private fun initializeMetricsSlider(){
        val duration=this.trailViewModel?.trail?.duration
        updateMetricsSlider(METRIC_DURATION, duration)
        this.metricsSlider.addOnValueChangedListener(this)
    }

    private fun initializeDurationText(){
        val durationToDisplay=MetricsHelper.displayDuration(context!!, this.trailViewModel?.trail?.duration)
        this.durationText.text=durationToDisplay
        this.durationText.setOnClickListener {
            val duration=this.trailViewModel?.trail?.duration
            updateCurrentMetricToSet(METRIC_DURATION)
            updateMetricsSlider(METRIC_DURATION, duration)
        }
    }

    private fun initializeAscentText(){
        val ascentToDisplay=MetricsHelper.displayAscent(
            context!!, this.trailViewModel?.trail?.ascent, true, true)
        this.ascentText.text=ascentToDisplay
        this.ascentText.setOnClickListener {
            val ascent=this.trailViewModel?.trail?.ascent
            updateCurrentMetricToSet(METRIC_ASCENT)
            updateMetricsSlider(METRIC_ASCENT, ascent)
        }
    }

    private fun initializeDescentText(){
        val descentToDisplay=MetricsHelper.displayDescent(
            context!!, this.trailViewModel?.trail?.descent, true, true)
        this.descentText.text=descentToDisplay
        this.descentText.setOnClickListener {
            val descent=this.trailViewModel?.trail?.descent
            updateCurrentMetricToSet(METRIC_DESCENT)
            updateMetricsSlider(METRIC_DESCENT, descent)
        }
    }

    private fun initializeDistanceText(){
        val distanceToDisplay=MetricsHelper.displayDistance(
            context!!, this.trailViewModel?.trail?.distance, true, true)
        this.distanceText.text=distanceToDisplay
        this.distanceText.setOnClickListener {
            val distance=this.trailViewModel?.trail?.distance
            updateCurrentMetricToSet(METRIC_DISTANCE)
            updateMetricsSlider(METRIC_DISTANCE, distance)
        }
    }

    private fun initializeMaxElevationText(){
        val maxElevationToDisplay=MetricsHelper.displayMaxElevation(
            context!!, this.trailViewModel?.trail?.maxElevation, true, true)
        this.maxElevationText.text=maxElevationToDisplay
        this.maxElevationText.setOnClickListener {
            val maxElevation=this.trailViewModel?.trail?.maxElevation
            updateCurrentMetricToSet(METRIC_MAX_ELEVATION)
            updateMetricsSlider(METRIC_MAX_ELEVATION, maxElevation)
        }
    }

    private fun initializeMinElevationText(){
        val minElevationToDisplay=MetricsHelper.displayMinElevation(
            context!!, this.trailViewModel?.trail?.minElevation, true, true)
        this.minElevationText.text=minElevationToDisplay
        this.minElevationText.setOnClickListener {
            val minElevation=this.trailViewModel?.trail?.minElevation
            updateCurrentMetricToSet(METRIC_MIN_ELEVATION)
            updateMetricsSlider(METRIC_MIN_ELEVATION, minElevation)
        }
    }

    private fun initializeResetMetricsButton(){
        this.resetMetricsButton.setOnClickListener {
            val value=when(this.currentMetricToSet){
                METRIC_DURATION -> {
                    this.trailViewModel?.trail?.autoCalculateDuration()
                    this.trailViewModel?.trail?.duration
                }
                METRIC_ASCENT -> {
                    this.trailViewModel?.trail?.autoCalculateAscent()
                    this.trailViewModel?.trail?.ascent
                }
                METRIC_DESCENT -> {
                    this.trailViewModel?.trail?.autoCalculateDescent()
                    this.trailViewModel?.trail?.descent
                }
                METRIC_DISTANCE -> {
                    this.trailViewModel?.trail?.autoCalculateDistance()
                    this.trailViewModel?.trail?.distance
                }
                METRIC_MAX_ELEVATION -> {
                    this.trailViewModel?.trail?.autoCalculateMaxElevation()
                    this.trailViewModel?.trail?.maxElevation
                }
                METRIC_MIN_ELEVATION -> {
                    this.trailViewModel?.trail?.autoCalculateMinElevation()
                    this.trailViewModel?.trail?.minElevation
                }
                else -> null
            }
            updateMetricsSlider(this.currentMetricToSet, value)
            updateCurrentMetric(value)
        }
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

    private fun updatePhoto(){
        Glide.with(context!!)
            .load(this.trailViewModel?.trail?.mainPhotoUrl)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_trail_black)
            .into(this.photoImageView)
    }

    fun onDeletePhotoButtonClick(view:View){
        deletePhoto()
    }

    fun onAddPhotoButtonClick(view:View){
        expandAddPhotoBottomSheet()
    }

    fun onSelectPhotoButtonClick(view:View){
        requestWritePermission()
    }

    fun onTakePhotoButtonClick(view:View){
        requestWriteAndCameraPermission()
    }

    fun onLocationTextFieldClick(view:View){
        (activity as TrailInfoEditActivity).searchLocation()
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
            METRIC_DURATION -> updateDuration(value?:this.trailViewModel?.trail?.duration)
            METRIC_ASCENT -> updateAscent(value?:this.trailViewModel?.trail?.ascent)
            METRIC_DESCENT -> updateDescent(value?:this.trailViewModel?.trail?.descent)
            METRIC_DISTANCE -> updateDistance(value?:this.trailViewModel?.trail?.distance?:0)
            METRIC_MAX_ELEVATION -> updateMaxElevation(value?:this.trailViewModel?.trail?.maxElevation)
            METRIC_MIN_ELEVATION -> updateMinElevation(value?:this.trailViewModel?.trail?.minElevation)
        }
    }

    private fun updateDuration(duration:Int?){
        this.trailViewModel?.trail?.duration=duration
        val durationToDisplay=MetricsHelper.displayDuration(context!!, duration)
        this.durationText.text=durationToDisplay
    }

    private fun updateAscent(ascent:Int?) {
        this.trailViewModel?.trail?.ascent = ascent
        val ascentToDisplay = MetricsHelper.displayAscent(
            context!!, ascent, true, true)
        this.ascentText.text = ascentToDisplay
    }

    private fun updateDescent(descent:Int?) {
        this.trailViewModel?.trail?.descent = descent
        val descentToDisplay = MetricsHelper.displayDescent(
            context!!, descent, true, true)
        this.descentText.text = descentToDisplay
    }

    private fun updateDistance(distance:Int) {
        this.trailViewModel?.trail?.distance = distance
        val distanceToDisplay = MetricsHelper.displayDistance(
            context!!, distance, true, true)
        this.distanceText.text = distanceToDisplay
    }

    private fun updateMaxElevation(elevation:Int?) {
        this.trailViewModel?.trail?.maxElevation = elevation
        val maxElevationToDisplay = MetricsHelper.displayMaxElevation(
            context!!, elevation, true, true)
        this.maxElevationText.text = maxElevationToDisplay
    }

    private fun updateMinElevation(elevation:Int?) {
        this.trailViewModel?.trail?.minElevation = elevation
        val minElevationToDisplay = MetricsHelper.displayMinElevation(
            context!!, elevation, true, true)
        this.minElevationText.text = minElevationToDisplay
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String){
        this.trailViewModel?.updateImagePathToUpload(false, filePath)
        this.trailViewModel?.trail?.mainPhotoUrl=filePath
        this.trailViewModel?.notifyDataChanged()
        updatePhoto()
    }

    override fun deletePhoto(){
        if(!this.trailViewModel?.trail?.mainPhotoUrl.isNullOrEmpty()) {
            this.trailViewModel?.updateImagePathToDelete(this.trailViewModel?.trail?.mainPhotoUrl!!)
            this.trailViewModel?.trail?.mainPhotoUrl = null
            this.trailViewModel?.notifyDataChanged()
            updatePhoto()
        }
    }
}
