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
import com.sildian.apps.togetrail.trail.model.dataRequests.TrailSaveDataRequest
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel

/*************************************************************************************************
 * Allows to edit information about a trail
 * @param trailViewModel : the trail data
 ************************************************************************************************/

class TrailInfoEditFragment(private val trailViewModel: TrailViewModel? = null)
    : BaseImagePickerFragment<FragmentTrailInfoEditBinding>()
{

    /**********************************Static items**********************************************/

    companion object {

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

    private var isTrailAlreadyLoaded = false
    var currentMetric = MutableLiveData<Int>()
    val currentValueFormatter = MutableLiveData<ValueFormatter>()
    val currentMaxValue = MutableLiveData<Int>()
    val currentValue = MutableLiveData<Int?>()

    /**************************************Life cycle********************************************/

    override fun onDestroy() {
        this.trailViewModel?.clearImagePaths()
        super.onDestroy()
    }

    /*********************************Data monitoring********************************************/

    override fun loadData() {
        initializeData()
        observeTrail()
        observeRequestSuccess()
        observeRequestFailure()
    }

    private fun initializeData() {
        this.binding.trailInfoEditFragment = this
        this.binding.trailViewModel = this.trailViewModel
    }

    private fun observeTrail() {
        this.trailViewModel?.data?.observe(this) { trail ->
            if (trail != null) {
                if (!isTrailAlreadyLoaded) {
                    setCurrentMetric(METRIC_DURATION)
                    isTrailAlreadyLoaded = true
                }
                refreshUI()
            }
        }
    }

    private fun observeRequestSuccess() {
        this.trailViewModel?.success?.observe(this) { success ->
            if (success != null && success is TrailSaveDataRequest) {
                onQuerySuccess()
            }
        }
    }

    private fun observeRequestFailure() {
        this.trailViewModel?.error?.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    override fun updateData(data:Any?) {
        if (data is Location) {
            this.trailViewModel?.data?.value?.location = data
            this.binding.fragmentTrailInfoEditTextFieldLocation.setText(data.fullAddress)
        }
    }

    private fun setCurrentMetric(metric: Int) {
        this.currentMetric.value = metric
        when (this.currentMetric.value) {
            METRIC_DURATION -> {
                this.currentValueFormatter.value = ValueFormatters.DurationValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_DURATION
                this.currentValue.value = trailViewModel?.data?.value?.duration
            }
            METRIC_ASCENT -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel?.data?.value?.ascent
            }
            METRIC_DESCENT -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel?.data?.value?.descent
            }
            METRIC_DISTANCE -> {
                this.currentValueFormatter.value = ValueFormatters.DistanceValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_DISTANCE
                this.currentValue.value = trailViewModel?.data?.value?.distance
            }
            METRIC_MAX_ELEVATION -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel?.data?.value?.maxElevation
            }
            METRIC_MIN_ELEVATION -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel?.data?.value?.minElevation
            }
        }
    }

    private fun setCurrentValue(value: Int) {
        when (this.currentMetric.value) {
            METRIC_DURATION -> {
                this.trailViewModel?.data?.value?.duration = value
                this.currentValue.value = this.trailViewModel?.data?.value?.duration
            }
            METRIC_ASCENT -> {
                this.trailViewModel?.data?.value?.ascent = value
                this.currentValue.value = this.trailViewModel?.data?.value?.ascent
            }
            METRIC_DESCENT -> {
                this.trailViewModel?.data?.value?.descent = value
                this.currentValue.value = this.trailViewModel?.data?.value?.descent
            }
            METRIC_DISTANCE -> {
                this.trailViewModel?.data?.value?.distance = value
                this.currentValue.value = this.trailViewModel?.data?.value?.distance
            }
            METRIC_MAX_ELEVATION -> {
                this.trailViewModel?.data?.value?.maxElevation = value
                this.currentValue.value = this.trailViewModel?.data?.value?.maxElevation
            }
            METRIC_MIN_ELEVATION -> {
                this.trailViewModel?.data?.value?.minElevation = value
                this.currentValue.value = this.trailViewModel?.data?.value?.minElevation
            }
        }
        this.trailViewModel?.notifyDataObserver()
    }

    private fun resetCurrentValue() {
        when (this.currentMetric.value) {
            METRIC_DURATION -> {
                this.trailViewModel?.data?.value?.autoCalculateDuration()
                this.currentValue.value = this.trailViewModel?.data?.value?.duration
            }
            METRIC_ASCENT -> {
                this.trailViewModel?.data?.value?.autoCalculateAscent()
                this.currentValue.value = this.trailViewModel?.data?.value?.ascent
            }
            METRIC_DESCENT -> {
                this.trailViewModel?.data?.value?.autoCalculateDescent()
                this.currentValue.value = this.trailViewModel?.data?.value?.descent
            }
            METRIC_DISTANCE -> {
                this.trailViewModel?.data?.value?.autoCalculateDistance()
                this.currentValue.value = this.trailViewModel?.data?.value?.distance
            }
            METRIC_MAX_ELEVATION -> {
                this.trailViewModel?.data?.value?.autoCalculateMaxElevation()
                this.currentValue.value = this.trailViewModel?.data?.value?.maxElevation
            }
            METRIC_MIN_ELEVATION -> {
                this.trailViewModel?.data?.value?.autoCalculateMinElevation()
                this.currentValue.value = this.trailViewModel?.data?.value?.minElevation
            }
        }
        this.trailViewModel?.notifyDataObserver()
    }

    override fun saveData() {
        if (checkDataIsValid()) {
            this.trailViewModel?.data?.value?.name = this.binding.fragmentTrailInfoEditTextFieldName.text.toString()
            this.trailViewModel?.data?.value?.level =
                TrailLevel.fromValue(this.binding.fragmentTrailInfoEditTextFieldDropdownLevel.tag.toString().toInt())
            this.trailViewModel?.data?.value?.loop = this.binding.fragmentTrailInfoEditSwitchLoop.isChecked
            this.trailViewModel?.data?.value?.description = this.binding.fragmentTrailInfoEditTextFieldDescription.text.toString()
            this.trailViewModel?.data?.value?.autoPopulatePosition()
            this.baseActivity?.showProgressDialog()
            this.trailViewModel?.saveTrail(false)
        }
    }

    override fun checkDataIsValid(): Boolean {
        if (checkTextFieldsAreNotEmpty()) {
            if (checkTextFieldsDropDownAreNotUnknown()) {
                return true
            } else {
                SnackbarHelper
                    .createSimpleSnackbar(this.binding.fragmentTrailInfoEditViewMessage, this.binding.fragmentTrailInfoEditBottomSheetAddPhoto, R.string.message_trail_level_unknown)
                    .show()
            }
        } else {
            SnackbarHelper
                .createSimpleSnackbar(this.binding.fragmentTrailInfoEditViewMessage, this.binding.fragmentTrailInfoEditBottomSheetAddPhoto, R.string.message_text_fields_empty)
                .show()
        }
        return false
    }

    private fun checkTextFieldsAreNotEmpty():Boolean{
        val textFieldsAndLayouts: HashMap<TextInputEditText, TextInputLayout> = hashMapOf()
        textFieldsAndLayouts[this.binding.fragmentTrailInfoEditTextFieldName] = this.binding.fragmentTrailInfoEditTextFieldLayoutName
        textFieldsAndLayouts[this.binding.fragmentTrailInfoEditTextFieldLocation] = this.binding.fragmentTrailInfoEditTextFieldLayoutLocation
        return TextFieldHelper.checkAllTextFieldsAreNotEmpty(textFieldsAndLayouts)
    }

    private fun checkTextFieldsDropDownAreNotUnknown():Boolean{
        return if (this.binding.fragmentTrailInfoEditTextFieldDropdownLevel.tag == 0) {
            this.binding.fragmentTrailInfoEditTextFieldDropdownLayoutLevel.error = getString(R.string.message_text_field_empty)
            false
        } else {
            this.binding.fragmentTrailInfoEditTextFieldDropdownLayoutLevel.error = null
            true
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_info_edit

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_trail_info_edit_bottom_sheet_add_photo

    override fun initializeUI() {
        refreshUI()
    }

    override fun refreshUI() {
        updateLevelTextFieldDropDown()
    }

    private fun updateLevelTextFieldDropDown(){
        val choice = resources.getStringArray(R.array.array_trail_levels)
        val initialValue = (this.trailViewModel?.data?.value?.level?.value?: TrailLevel.MEDIUM.value)
        DropdownMenuHelper.populateDropdownMenu(this.binding.fragmentTrailInfoEditTextFieldDropdownLevel, choice, initialValue)
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
            this.binding.fragmentTrailInfoEditTextDuration ->
                setCurrentMetric(METRIC_DURATION)
            this.binding.fragmentTrailInfoEditTextAscent ->
                setCurrentMetric(METRIC_ASCENT)
            this.binding.fragmentTrailInfoEditTextDescent ->
                setCurrentMetric(METRIC_DESCENT)
            this.binding.fragmentTrailInfoEditTextDistance ->
                setCurrentMetric(METRIC_DISTANCE)
            this.binding.fragmentTrailInfoEditTextMaxElevation ->
                setCurrentMetric(METRIC_MAX_ELEVATION)
            this.binding.fragmentTrailInfoEditTextMinElevation ->
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
        this.trailViewModel?.data?.value?.mainPhotoUrl = filePath
        this.trailViewModel?.notifyDataChanged()
    }

    override fun deletePhoto() {
        if (!this.trailViewModel?.data?.value?.mainPhotoUrl.isNullOrEmpty()) {
            this.trailViewModel?.updateImagePathToDelete(this.trailViewModel.data.value?.mainPhotoUrl!!)
            this.trailViewModel?.data?.value?.mainPhotoUrl = null
            this.trailViewModel?.notifyDataChanged()
        }
    }
}
