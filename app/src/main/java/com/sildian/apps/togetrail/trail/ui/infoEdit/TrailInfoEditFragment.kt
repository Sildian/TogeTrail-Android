package com.sildian.apps.togetrail.trail.ui.infoEdit

import android.view.View
import androidx.fragment.app.activityViewModels
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
import com.sildian.apps.togetrail.location.data.core.Location
import com.sildian.apps.togetrail.trail.data.core.TrailLevel
import com.sildian.apps.togetrail.trail.data.dataRequests.TrailSaveDataRequest
import com.sildian.apps.togetrail.trail.data.viewModels.TrailViewModel
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Allows to edit information about a trail
 ************************************************************************************************/

@AndroidEntryPoint
class TrailInfoEditFragment : BaseImagePickerFragment<FragmentTrailInfoEditBinding>() {

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

    private val trailViewModel: TrailViewModel by activityViewModels()
    private var isTrailAlreadyLoaded = false
    var currentMetric = MutableLiveData<Int>()
    val currentValueFormatter = MutableLiveData<ValueFormatter>()
    val currentMaxValue = MutableLiveData<Int>()
    val currentValue = MutableLiveData<Int?>()

    /**************************************Life cycle********************************************/

    override fun onDestroy() {
        this.trailViewModel.clearImagePaths()
        super.onDestroy()
    }

    /*********************************Data monitoring********************************************/

    override fun initializeData() {
        this.binding.trailInfoEditFragment = this
        this.binding.trailViewModel = this.trailViewModel
        observeTrail()
        observeDataRequestState()
    }

    private fun observeTrail() {
        this.trailViewModel.data.observe(this) { trailData ->
            trailData?.error?.let { e ->
                onQueryError(e)
            } ?:
            trailData?.data?.let { trail ->
                if (!isTrailAlreadyLoaded) {
                    setCurrentMetric(METRIC_DURATION)
                    isTrailAlreadyLoaded = true
                }
                refreshUI()
            }
        }
    }

    private fun observeDataRequestState() {
        this.trailViewModel.dataRequestState.observe(this) { dataRequestState ->
            if (dataRequestState?.dataRequest is TrailSaveDataRequest) {
                dataRequestState.error?.let { e ->
                    onQueryError(e)
                } ?: onQuerySuccess()
            }
        }
    }

    override fun updateData(data:Any?) {
        if (data is Location) {
            this.trailViewModel.data.value?.data?.location = data
            this.binding.fragmentTrailInfoEditTextFieldLocation.setText(data.fullAddress)
        }
    }

    private fun setCurrentMetric(metric: Int) {
        this.currentMetric.value = metric
        when (this.currentMetric.value) {
            METRIC_DURATION -> {
                this.currentValueFormatter.value = ValueFormatters.DurationValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_DURATION
                this.currentValue.value = trailViewModel.data.value?.data?.duration
            }
            METRIC_ASCENT -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel.data.value?.data?.ascent
            }
            METRIC_DESCENT -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel.data.value?.data?.descent
            }
            METRIC_DISTANCE -> {
                this.currentValueFormatter.value = ValueFormatters.DistanceValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_DISTANCE
                this.currentValue.value = trailViewModel.data.value?.data?.distance
            }
            METRIC_MAX_ELEVATION -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel.data.value?.data?.maxElevation
            }
            METRIC_MIN_ELEVATION -> {
                this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
                this.currentMaxValue.value = VALUE_MAX_ALTITUDE
                this.currentValue.value = trailViewModel.data.value?.data?.minElevation
            }
        }
    }

    private fun setCurrentValue(value: Int) {
        when (this.currentMetric.value) {
            METRIC_DURATION -> {
                this.trailViewModel.data.value?.data?.duration = value
                this.currentValue.value = this.trailViewModel.data.value?.data?.duration
            }
            METRIC_ASCENT -> {
                this.trailViewModel.data.value?.data?.ascent = value
                this.currentValue.value = this.trailViewModel.data.value?.data?.ascent
            }
            METRIC_DESCENT -> {
                this.trailViewModel.data.value?.data?.descent = value
                this.currentValue.value = this.trailViewModel.data.value?.data?.descent
            }
            METRIC_DISTANCE -> {
                this.trailViewModel.data.value?.data?.distance = value
                this.currentValue.value = this.trailViewModel.data.value?.data?.distance
            }
            METRIC_MAX_ELEVATION -> {
                this.trailViewModel.data.value?.data?.maxElevation = value
                this.currentValue.value = this.trailViewModel.data.value?.data?.maxElevation
            }
            METRIC_MIN_ELEVATION -> {
                this.trailViewModel.data.value?.data?.minElevation = value
                this.currentValue.value = this.trailViewModel.data.value?.data?.minElevation
            }
        }
        this.trailViewModel.notifyDataObserver()
    }

    private fun resetCurrentValue() {
        when (this.currentMetric.value) {
            METRIC_DURATION -> {
                this.trailViewModel.data.value?.data?.autoCalculateDuration()
                this.currentValue.value = this.trailViewModel.data.value?.data?.duration
            }
            METRIC_ASCENT -> {
                this.trailViewModel.data.value?.data?.autoCalculateAscent()
                this.currentValue.value = this.trailViewModel.data.value?.data?.ascent
            }
            METRIC_DESCENT -> {
                this.trailViewModel.data.value?.data?.autoCalculateDescent()
                this.currentValue.value = this.trailViewModel.data.value?.data?.descent
            }
            METRIC_DISTANCE -> {
                this.trailViewModel.data.value?.data?.autoCalculateDistance()
                this.currentValue.value = this.trailViewModel.data.value?.data?.distance
            }
            METRIC_MAX_ELEVATION -> {
                this.trailViewModel.data.value?.data?.autoCalculateMaxElevation()
                this.currentValue.value = this.trailViewModel.data.value?.data?.maxElevation
            }
            METRIC_MIN_ELEVATION -> {
                this.trailViewModel.data.value?.data?.autoCalculateMinElevation()
                this.currentValue.value = this.trailViewModel.data.value?.data?.minElevation
            }
        }
        this.trailViewModel.notifyDataObserver()
    }

    override fun saveData() {
        if (checkDataIsValid()) {
            this.trailViewModel.data.value?.data?.name = this.binding.fragmentTrailInfoEditTextFieldName.text.toString()
            this.trailViewModel.data.value?.data?.level =
                TrailLevel.fromValue(this.binding.fragmentTrailInfoEditTextFieldDropdownLevel.tag.toString().toInt())
            this.trailViewModel.data.value?.data?.loop = this.binding.fragmentTrailInfoEditSwitchLoop.isChecked
            this.trailViewModel.data.value?.data?.description = this.binding.fragmentTrailInfoEditTextFieldDescription.text.toString()
            this.trailViewModel.data.value?.data?.autoPopulatePosition()
            this.baseActivity?.showProgressDialog()
            this.trailViewModel.saveTrail(false)
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
        val initialValue = (this.trailViewModel.data.value?.data?.level?.value?: TrailLevel.MEDIUM.value)
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
        this.trailViewModel.updateImagePathToUpload(false, filePath)
        this.trailViewModel.data.value?.data?.mainPhotoUrl = filePath
        this.trailViewModel.notifyDataChanged()
    }

    override fun deletePhoto() {
        if (!this.trailViewModel.data.value?.data?.mainPhotoUrl.isNullOrEmpty()) {
            this.trailViewModel.updateImagePathToDelete(this.trailViewModel.data.value?.data?.mainPhotoUrl!!)
            this.trailViewModel.data.value?.data?.mainPhotoUrl = null
            this.trailViewModel.notifyDataChanged()
        }
    }
}
