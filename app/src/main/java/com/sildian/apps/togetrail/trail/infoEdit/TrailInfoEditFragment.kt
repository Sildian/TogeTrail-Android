package com.sildian.apps.togetrail.trail.infoEdit

import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sildian.apps.circularsliderlibrary.CircularSlider
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.uiHelpers.DropdownMenuHelper
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.ValueFormatters
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

    /**************************************Life cycle********************************************/

    override fun onDestroy() {
        this.trailViewModel?.clearImagePaths()
        super.onDestroy()
    }

    /*********************************Data monitoring********************************************/

    override fun loadData() {
        initializeData()
        observeTrail()
        observeSaveRequestSuccess()
        observeRequestFailure()
    }

    private fun initializeData() {
        this.binding.lifecycleOwner = this
        (this.binding as FragmentTrailInfoEditBinding).trailInfoEditFragment=this
        (this.binding as FragmentTrailInfoEditBinding).trailViewModel=this.trailViewModel
    }

    private fun observeTrail() {
        this.trailViewModel?.trail?.observe(this) { trail ->
            if (trail != null) {
                refreshUI()
            }
        }
    }

    private fun observeSaveRequestSuccess() {
        this.trailViewModel?.saveRequestSuccess?.observe(this) { success ->
            if (success) {
                onSaveSuccess()
            }
        }
    }

    private fun observeRequestFailure() {
        this.trailViewModel?.requestFailure?.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    override fun updateData(data:Any?) {
        if(data is Location){
            this.trailViewModel?.trail?.value?.location=data
            this.locationTextField.setText(data.fullAddress)
        }
    }

    override fun saveData() {
        if(checkDataIsValid()) {
            this.trailViewModel?.trail?.value?.name = this.nameTextField.text.toString()
            this.trailViewModel?.trail?.value?.level =
                TrailLevel.fromValue(this.levelTextFieldDropDown.tag.toString().toInt())
            this.trailViewModel?.trail?.value?.loop=this.loopSwitch.isChecked
            this.trailViewModel?.trail?.value?.description = this.descriptionTextField.text.toString()
            this.trailViewModel?.trail?.value?.autoPopulatePosition()
            this.baseActivity?.showProgressDialog()
            this.trailViewModel?.saveTrailInDatabase(false)
        }
    }

    override fun checkDataIsValid(): Boolean {
        if(checkTextFieldsAreNotEmpty()){
            if(checkTextFieldsDropDownAreNotUnknown()){
                return true
            }else{
                SnackbarHelper
                    .createSimpleSnackbar(this.messageView, this.messageAnchorView, R.string.message_trail_level_unknown)
                    .show()
            }
        }else{
            SnackbarHelper
                .createSimpleSnackbar(this.messageView, this.messageAnchorView, R.string.message_text_fields_empty)
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
        initializeResetMetricsButton()
        refreshUI()
    }

    override fun refreshUI() {
        updateLevelTextFieldDropDown()
        updateMetricsSlider()
        updateDurationText()
        updateAscentText()
        updateDescentText()
        updateDistanceText()
        updateMaxElevationText()
        updateMinElevationText()
        updateCurrentMetricToSet(METRIC_DURATION)
        updatePhoto()
    }

    private fun updateLevelTextFieldDropDown(){
        val choice=resources.getStringArray(R.array.array_trail_levels)
        val initialValue=(this.trailViewModel?.trail?.value?.level?.value?: TrailLevel.MEDIUM.value)
        DropdownMenuHelper.populateDropdownMenu(this.levelTextFieldDropDown, choice, initialValue)
    }

    private fun initializeMetricsSlider(){
        this.metricsSlider.addOnValueChangedListener(this)
    }

    private fun updateMetricsSlider() {
        val duration=this.trailViewModel?.trail?.value?.duration
        updateMetricsSlider(METRIC_DURATION, duration)
    }

    private fun updateDurationText(){
        val durationToDisplay=MetricsHelper.displayDuration(context!!, this.trailViewModel?.trail?.value?.duration)
        this.durationText.text=durationToDisplay
        this.durationText.setOnClickListener {
            val duration=this.trailViewModel?.trail?.value?.duration
            updateCurrentMetricToSet(METRIC_DURATION)
            updateMetricsSlider(METRIC_DURATION, duration)
        }
    }

    private fun updateAscentText(){
        val ascentToDisplay=MetricsHelper.displayAscent(
            context!!, this.trailViewModel?.trail?.value?.ascent, true, true)
        this.ascentText.text=ascentToDisplay
        this.ascentText.setOnClickListener {
            val ascent=this.trailViewModel?.trail?.value?.ascent
            updateCurrentMetricToSet(METRIC_ASCENT)
            updateMetricsSlider(METRIC_ASCENT, ascent)
        }
    }

    private fun updateDescentText(){
        val descentToDisplay=MetricsHelper.displayDescent(
            context!!, this.trailViewModel?.trail?.value?.descent, true, true)
        this.descentText.text=descentToDisplay
        this.descentText.setOnClickListener {
            val descent=this.trailViewModel?.trail?.value?.descent
            updateCurrentMetricToSet(METRIC_DESCENT)
            updateMetricsSlider(METRIC_DESCENT, descent)
        }
    }

    private fun updateDistanceText(){
        val distanceToDisplay=MetricsHelper.displayDistance(
            context!!, this.trailViewModel?.trail?.value?.distance, true, true)
        this.distanceText.text=distanceToDisplay
        this.distanceText.setOnClickListener {
            val distance=this.trailViewModel?.trail?.value?.distance
            updateCurrentMetricToSet(METRIC_DISTANCE)
            updateMetricsSlider(METRIC_DISTANCE, distance)
        }
    }

    private fun updateMaxElevationText(){
        val maxElevationToDisplay=MetricsHelper.displayMaxElevation(
            context!!, this.trailViewModel?.trail?.value?.maxElevation, true, true)
        this.maxElevationText.text=maxElevationToDisplay
        this.maxElevationText.setOnClickListener {
            val maxElevation=this.trailViewModel?.trail?.value?.maxElevation
            updateCurrentMetricToSet(METRIC_MAX_ELEVATION)
            updateMetricsSlider(METRIC_MAX_ELEVATION, maxElevation)
        }
    }

    private fun updateMinElevationText(){
        val minElevationToDisplay=MetricsHelper.displayMinElevation(
            context!!, this.trailViewModel?.trail?.value?.minElevation, true, true)
        this.minElevationText.text=minElevationToDisplay
        this.minElevationText.setOnClickListener {
            val minElevation=this.trailViewModel?.trail?.value?.minElevation
            updateCurrentMetricToSet(METRIC_MIN_ELEVATION)
            updateMetricsSlider(METRIC_MIN_ELEVATION, minElevation)
        }
    }

    private fun initializeResetMetricsButton(){
        this.resetMetricsButton.setOnClickListener {
            val value=when(this.currentMetricToSet){
                METRIC_DURATION -> {
                    this.trailViewModel?.trail?.value?.autoCalculateDuration()
                    this.trailViewModel?.trail?.value?.duration
                }
                METRIC_ASCENT -> {
                    this.trailViewModel?.trail?.value?.autoCalculateAscent()
                    this.trailViewModel?.trail?.value?.ascent
                }
                METRIC_DESCENT -> {
                    this.trailViewModel?.trail?.value?.autoCalculateDescent()
                    this.trailViewModel?.trail?.value?.descent
                }
                METRIC_DISTANCE -> {
                    this.trailViewModel?.trail?.value?.autoCalculateDistance()
                    this.trailViewModel?.trail?.value?.distance
                }
                METRIC_MAX_ELEVATION -> {
                    this.trailViewModel?.trail?.value?.autoCalculateMaxElevation()
                    this.trailViewModel?.trail?.value?.maxElevation
                }
                METRIC_MIN_ELEVATION -> {
                    this.trailViewModel?.trail?.value?.autoCalculateMinElevation()
                    this.trailViewModel?.trail?.value?.minElevation
                }
                else -> null
            }
            updateMetricsSlider(currentMetricToSet, value)
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
            .load(this.trailViewModel?.trail?.value?.mainPhotoUrl)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_trail_black)
            .into(this.photoImageView)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onDeletePhotoButtonClick(view:View){
        deletePhoto()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddPhotoButtonClick(view:View){
        expandAddPhotoBottomSheet()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSelectPhotoButtonClick(view:View){
        requestWritePermission()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTakePhotoButtonClick(view:View){
        requestWriteAndCameraPermission()
    }

    @Suppress("UNUSED_PARAMETER")
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
                this.metricsSlider.valueFormatter= ValueFormatters.DurationValueFormatter()
                this.metricsSlider.setMaxValue(VALUE_MAX_DURATION)
            }
            METRIC_DISTANCE -> {
                this.metricsSlider.valueFormatter= ValueFormatters.DistanceValueFormatter()
                this.metricsSlider.setMaxValue(VALUE_MAX_DISTANCE)
            }
            else -> {
                this.metricsSlider.valueFormatter= ValueFormatters.AltitudeValueFormatter()
                this.metricsSlider.setMaxValue(VALUE_MAX_ALTITUDE)
            }
        }
        this.metricsSlider.setCurrentValue(currentValue?:0)
    }

    private fun updateCurrentMetric(value:Int?){
        when(this.currentMetricToSet){
            METRIC_DURATION -> updateDuration(value?:this.trailViewModel?.trail?.value?.duration)
            METRIC_ASCENT -> updateAscent(value?:this.trailViewModel?.trail?.value?.ascent)
            METRIC_DESCENT -> updateDescent(value?:this.trailViewModel?.trail?.value?.descent)
            METRIC_DISTANCE -> updateDistance(value?:this.trailViewModel?.trail?.value?.distance?:0)
            METRIC_MAX_ELEVATION -> updateMaxElevation(value?:this.trailViewModel?.trail?.value?.maxElevation)
            METRIC_MIN_ELEVATION -> updateMinElevation(value?:this.trailViewModel?.trail?.value?.minElevation)
        }
    }

    private fun updateDuration(duration:Int?){
        this.trailViewModel?.trail?.value?.duration=duration
        val durationToDisplay=MetricsHelper.displayDuration(context!!, duration)
        this.durationText.text=durationToDisplay
    }

    private fun updateAscent(ascent:Int?) {
        this.trailViewModel?.trail?.value?.ascent = ascent
        val ascentToDisplay = MetricsHelper.displayAscent(
            context!!, ascent, true, true)
        this.ascentText.text = ascentToDisplay
    }

    private fun updateDescent(descent:Int?) {
        this.trailViewModel?.trail?.value?.descent = descent
        val descentToDisplay = MetricsHelper.displayDescent(
            context!!, descent, true, true)
        this.descentText.text = descentToDisplay
    }

    private fun updateDistance(distance:Int) {
        this.trailViewModel?.trail?.value?.distance = distance
        val distanceToDisplay = MetricsHelper.displayDistance(
            context!!, distance, true, true)
        this.distanceText.text = distanceToDisplay
    }

    private fun updateMaxElevation(elevation:Int?) {
        this.trailViewModel?.trail?.value?.maxElevation = elevation
        val maxElevationToDisplay = MetricsHelper.displayMaxElevation(
            context!!, elevation, true, true)
        this.maxElevationText.text = maxElevationToDisplay
    }

    private fun updateMinElevation(elevation:Int?) {
        this.trailViewModel?.trail?.value?.minElevation = elevation
        val minElevationToDisplay = MetricsHelper.displayMinElevation(
            context!!, elevation, true, true)
        this.minElevationText.text = minElevationToDisplay
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String){
        this.trailViewModel?.updateImagePathToUpload(false, filePath)
        this.trailViewModel?.trail?.value?.mainPhotoUrl=filePath
        this.trailViewModel?.notifyDataChanged()
        updatePhoto()
    }

    override fun deletePhoto(){
        if(!this.trailViewModel?.trail?.value?.mainPhotoUrl.isNullOrEmpty()) {
            this.trailViewModel?.updateImagePathToDelete(this.trailViewModel.trail.value?.mainPhotoUrl!!)
            this.trailViewModel?.trail?.value?.mainPhotoUrl = null
            this.trailViewModel?.notifyDataChanged()
            updatePhoto()
        }
    }
}
