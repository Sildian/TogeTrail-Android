package com.sildian.apps.togetrail.trail.infoEdit

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sildian.apps.circularsliderlibrary.CircularSlider
import com.sildian.apps.circularsliderlibrary.ValueFormatter
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.uiHelpers.DropdownMenuHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.ValueFormatters
import com.sildian.apps.togetrail.databinding.FragmentTrailInfoEditBinding
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import kotlinx.android.synthetic.main.fragment_trail_info_edit.*
import kotlinx.android.synthetic.main.fragment_trail_info_edit.view.*

/*************************************************************************************************
 * Allows to edit information about a trail
 * @param trailViewModel : the trail data
 ************************************************************************************************/

class TrailInfoEditFragment(private val trailViewModel: TrailViewModel? = null)
    : BaseImagePickerFragment()
{

    /**********************************Static items**********************************************/

    companion object{

        /**Metrics to set with the slider**/
        private const val METRIC_DURATION = 0
        private const val METRIC_ASCENT = 1
        private const val METRIC_DESCENT = 2
        private const val METRIC_DISTANCE = 3
        private const val METRIC_MAX_ELEVATION = 4
        private const val METRIC_MIN_ELEVATION = 5

        /**Values max**/
        private const val VALUE_MAX_DURATION = 720      //Max value for a duration (in minutes)
        private const val VALUE_MAX_DISTANCE = 30000    //Max value for a distance (in meters)
        private const val VALUE_MAX_ALTITUDE = 4000     //Max value for an altitude (in meters)
    }

    /***************************************Data*************************************************/

    var currentMetricToSet = METRIC_DURATION
    val currentValueFormatter = MutableLiveData<ValueFormatter>(ValueFormatters.DurationValueFormatter())
    val currentMaxValue = MutableLiveData(VALUE_MAX_DURATION)
    val currentValue = MutableLiveData<Int>(trailViewModel?.trail?.value?.duration)

    /**********************************UI component**********************************************/

    private val nameTextFieldLayout by lazy {layout.fragment_trail_info_edit_text_field_layout_name}
    private val nameTextField by lazy {layout.fragment_trail_info_edit_text_field_name}
    private val levelTextFieldDropDownLayout by lazy {layout.fragment_trail_info_edit_text_field_dropdown_layout_level}
    private val levelTextFieldDropDown by lazy {layout.fragment_trail_info_edit_text_field_dropdown_level}
    private val loopSwitch by lazy {layout.fragment_trail_info_edit_switch_loop}
    private val locationTextFieldLayout by lazy {layout.fragment_trail_info_edit_text_field_layout_location}
    private val locationTextField by lazy {layout.fragment_trail_info_edit_text_field_location}
    private val descriptionTextField by lazy {layout.fragment_trail_info_edit_text_field_description}
    private val messageView by lazy {layout.fragment_trail_info_edit_view_message}
    private val messageAnchorView by lazy {layout.fragment_trail_info_edit_bottom_sheet_add_photo}

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
        (this.binding as FragmentTrailInfoEditBinding).trailInfoEditFragment = this
        (this.binding as FragmentTrailInfoEditBinding).trailViewModel = this.trailViewModel
    }

    private fun observeTrail() {
        this.trailViewModel?.trail?.observe(this) { trail ->
            if (trail != null) {
                setCurrentMetric(METRIC_DURATION)
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
        if (data is Location) {
            this.trailViewModel?.trail?.value?.location = data
            this.locationTextField.setText(data.fullAddress)
        }
    }

    private fun setCurrentMetric(metric: Int) {
        this.currentMetricToSet = metric
        when (this.currentMetricToSet) {
            METRIC_DURATION -> {
                this.currentValueFormatter.value = ValueFormatters.DurationValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_DURATION
                this.currentValue.value = trailViewModel?.trail?.value?.duration
            }
            METRIC_ASCENT -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel?.trail?.value?.ascent
            }
            METRIC_DESCENT -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel?.trail?.value?.descent
            }
            METRIC_DISTANCE -> {
                this.currentValueFormatter.value = ValueFormatters.DistanceValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_DISTANCE
                this.currentValue.value = trailViewModel?.trail?.value?.distance
            }
            METRIC_MAX_ELEVATION -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel?.trail?.value?.maxElevation
            }
            METRIC_MIN_ELEVATION -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel?.trail?.value?.minElevation
            }
        }
    }

    private fun setCurrentValue(value: Int) {
        when (this.currentMetricToSet) {
            METRIC_DURATION -> {
                this.trailViewModel?.trail?.value?.duration = value
                this.currentValue.value = this.trailViewModel?.trail?.value?.duration
            }
            METRIC_ASCENT -> {
                this.trailViewModel?.trail?.value?.ascent = value
                this.currentValue.value = this.trailViewModel?.trail?.value?.ascent
            }
            METRIC_DESCENT -> {
                this.trailViewModel?.trail?.value?.descent = value
                this.currentValue.value = this.trailViewModel?.trail?.value?.descent
            }
            METRIC_DISTANCE -> {
                this.trailViewModel?.trail?.value?.distance = value
                this.currentValue.value = this.trailViewModel?.trail?.value?.distance
            }
            METRIC_MAX_ELEVATION -> {
                this.trailViewModel?.trail?.value?.maxElevation = value
                this.currentValue.value = this.trailViewModel?.trail?.value?.maxElevation
            }
            METRIC_MIN_ELEVATION -> {
                this.trailViewModel?.trail?.value?.minElevation = value
                this.currentValue.value = this.trailViewModel?.trail?.value?.minElevation
            }
        }
        this.trailViewModel?.trail?.value = this.trailViewModel?.trail?.value
    }

    private fun resetCurrentValue() {
        when (this.currentMetricToSet) {
            METRIC_DURATION -> {
                this.trailViewModel?.trail?.value?.autoCalculateDuration()
                this.currentValue.value = this.trailViewModel?.trail?.value?.duration
            }
            METRIC_ASCENT -> {
                this.trailViewModel?.trail?.value?.autoCalculateAscent()
                this.currentValue.value = this.trailViewModel?.trail?.value?.ascent
            }
            METRIC_DESCENT -> {
                this.trailViewModel?.trail?.value?.autoCalculateDescent()
                this.currentValue.value = this.trailViewModel?.trail?.value?.descent
            }
            METRIC_DISTANCE -> {
                this.trailViewModel?.trail?.value?.autoCalculateDistance()
                this.currentValue.value = this.trailViewModel?.trail?.value?.distance
            }
            METRIC_MAX_ELEVATION -> {
                this.trailViewModel?.trail?.value?.autoCalculateMaxElevation()
                this.currentValue.value = this.trailViewModel?.trail?.value?.maxElevation
            }
            METRIC_MIN_ELEVATION -> {
                this.trailViewModel?.trail?.value?.autoCalculateMinElevation()
                this.currentValue.value = this.trailViewModel?.trail?.value?.minElevation
            }
        }
        this.trailViewModel?.trail?.value = this.trailViewModel?.trail?.value
    }

    override fun saveData() {
        if (checkDataIsValid()) {
            this.trailViewModel?.trail?.value?.name = this.nameTextField.text.toString()
            this.trailViewModel?.trail?.value?.level =
                TrailLevel.fromValue(this.levelTextFieldDropDown.tag.toString().toInt())
            this.trailViewModel?.trail?.value?.loop = this.loopSwitch.isChecked
            this.trailViewModel?.trail?.value?.description = this.descriptionTextField.text.toString()
            this.trailViewModel?.trail?.value?.autoPopulatePosition()
            this.baseActivity?.showProgressDialog()
            this.trailViewModel?.saveTrailInDatabase(false)
        }
    }

    override fun checkDataIsValid(): Boolean {
        if (checkTextFieldsAreNotEmpty()) {
            if (checkTextFieldsDropDownAreNotUnknown()) {
                return true
            } else {
                SnackbarHelper
                    .createSimpleSnackbar(this.messageView, this.messageAnchorView, R.string.message_trail_level_unknown)
                    .show()
            }
        } else {
            SnackbarHelper
                .createSimpleSnackbar(this.messageView, this.messageAnchorView, R.string.message_text_fields_empty)
                .show()
        }
        return false
    }

    private fun checkTextFieldsAreNotEmpty():Boolean{
        val textFieldsAndLayouts: HashMap<TextInputEditText, TextInputLayout> = hashMapOf()
        textFieldsAndLayouts[this.nameTextField] = this.nameTextFieldLayout
        textFieldsAndLayouts[this.locationTextField] = this.locationTextFieldLayout
        return TextFieldHelper.checkAllTextFieldsAreNotEmpty(textFieldsAndLayouts)
    }

    private fun checkTextFieldsDropDownAreNotUnknown():Boolean{
        return if (this.levelTextFieldDropDown.tag == 0) {
            this.levelTextFieldDropDownLayout.error = getString(R.string.message_text_field_empty)
            false
        } else {
            this.levelTextFieldDropDownLayout.error = null
            true
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_info_edit

    override fun useDataBinding(): Boolean = true

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_trail_info_edit_bottom_sheet_add_photo

    override fun initializeUI() {
        refreshUI()
    }

    override fun refreshUI() {
        updateLevelTextFieldDropDown()
    }

    private fun updateLevelTextFieldDropDown(){
        val choice=resources.getStringArray(R.array.array_trail_levels)
        val initialValue=(this.trailViewModel?.trail?.value?.level?.value?: TrailLevel.MEDIUM.value)
        DropdownMenuHelper.populateDropdownMenu(this.levelTextFieldDropDown, choice, initialValue)
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
    fun onMetricsSliderValueChanged(view: CircularSlider, value: Int) {
        setCurrentValue(value)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onValueClick(view: View) {
        when (view) {
            fragment_trail_info_edit_text_duration ->
                setCurrentMetric(METRIC_DURATION)
            fragment_trail_info_edit_text_ascent ->
                setCurrentMetric(METRIC_ASCENT)
            fragment_trail_info_edit_text_descent ->
                setCurrentMetric(METRIC_DESCENT)
            fragment_trail_info_edit_text_distance ->
                setCurrentMetric(METRIC_DISTANCE)
            fragment_trail_info_edit_text_max_elevation ->
                setCurrentMetric(METRIC_MAX_ELEVATION)
            fragment_trail_info_edit_text_min_elevation ->
                setCurrentMetric(METRIC_MIN_ELEVATION)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onResetMetricsButtonClick(view: View) {
        resetCurrentValue()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSelectPhotoButtonClick(view:View) {
        requestWritePermission()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTakePhotoButtonClick(view:View) {
        requestWriteAndCameraPermission()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLocationTextFieldClick(view:View) {
        (activity as TrailInfoEditActivity).searchLocation()
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String) {
        this.trailViewModel?.updateImagePathToUpload(false, filePath)
        this.trailViewModel?.trail?.value?.mainPhotoUrl=filePath
        this.trailViewModel?.notifyDataChanged()
    }

    override fun deletePhoto() {
        if (!this.trailViewModel?.trail?.value?.mainPhotoUrl.isNullOrEmpty()) {
            this.trailViewModel?.updateImagePathToDelete(this.trailViewModel.trail.value?.mainPhotoUrl!!)
            this.trailViewModel?.trail?.value?.mainPhotoUrl = null
            this.trailViewModel?.notifyDataChanged()
        }
    }
}
