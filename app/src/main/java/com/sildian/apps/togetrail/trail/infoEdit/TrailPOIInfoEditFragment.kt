package com.sildian.apps.togetrail.trail.infoEdit

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.sildian.apps.circularsliderlibrary.CircularSlider
import com.sildian.apps.circularsliderlibrary.ValueFormatter
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.ValueFormatters
import com.sildian.apps.togetrail.databinding.FragmentTrailPoiInfoEditBinding
import com.sildian.apps.togetrail.trail.model.dataRequests.TrailSaveDataRequest
import com.sildian.apps.togetrail.trail.model.viewModels.TrailViewModel
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Allows to edit information about a trailPointOfInterest
 * @param trailViewModel : the trail data
 ************************************************************************************************/

@AndroidEntryPoint
class TrailPOIInfoEditFragment(
    private val trailViewModel: TrailViewModel? = null,
    private val trailPointOfInterestPosition:Int? = null
)
    : BaseImagePickerFragment<FragmentTrailPoiInfoEditBinding>()
{

    /**********************************Static items**********************************************/

    companion object{

        /**Values max**/
        private const val VALUE_MAX_ALTITUDE = 4000     //Max value for an altitude (in meters)
    }

    /**************************************Data**************************************************/

    val currentValueFormatter = MutableLiveData<ValueFormatter>(ValueFormatters.AltitudeValueFormatter())
    val currentMaxValue = MutableLiveData(VALUE_MAX_ALTITUDE)

    /**************************************Life cycle********************************************/

    override fun onDestroy() {
        this.trailViewModel?.clearImagePaths()
        super.onDestroy()
    }

    /*********************************Data monitoring********************************************/

    override fun loadData() {
        initializeData()
        observeTrail()
        observeTrailPOI()
        observeDataRequestState()
    }

    private fun initializeData() {
        this.binding.trailPOIInfoEditFragment = this
        this.binding.trailViewModel = this.trailViewModel
        this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
        this.currentMaxValue.value = VALUE_MAX_ALTITUDE
    }

    private fun observeTrail() {
        this.trailViewModel?.data?.observe(this) { trailData ->
            trailData?.error?.let { e ->
                onQueryError(e)
            } ?:
            trailData?.data?.let { trail ->
                this.trailPointOfInterestPosition?.let { position ->
                    this.trailViewModel.watchPointOfInterest(position)
                }
            }
        }
    }

    private fun observeTrailPOI() {
        this.trailViewModel?.trailPointOfInterest?.observe(this) { trailPOIData ->
            trailPOIData?.error?.let { e ->
                onQueryError(e)
            } ?:
            trailPOIData?.data?.let { trailPOI ->
                refreshUI()
            }
        }
    }

    private fun observeDataRequestState() {
        this.trailViewModel?.dataRequestState?.observe(this) { dataRequestState ->
            if (dataRequestState?.dataRequest is TrailSaveDataRequest) {
                dataRequestState.error?.let { e ->
                    onQueryError(e)
                } ?: onQuerySuccess()
            }
        }
    }

    private fun setValue(value: Int?) {
        this.trailViewModel?.trailPointOfInterest?.value?.data?.elevation = value
        this.trailViewModel?.notifyDataObserver()
    }

    override fun saveData() {
        if (checkDataIsValid()) {
            if (this.trailViewModel?.trailPointOfInterest?.value != null) {
                this.trailViewModel.trailPointOfInterest.value?.data?.name = this.binding.fragmentTrailPoiInfoEditTextFieldName.text.toString()
                this.trailViewModel.trailPointOfInterest.value?.data?.description = this.binding.fragmentTrailPoiInfoEditTextFieldDescription.text.toString()
                this.baseActivity?.showProgressDialog()
                this.trailViewModel.saveTrail(true)
            }
        }
    }

    override fun checkDataIsValid(): Boolean {
        if (checkTextFieldsAreNotEmpty()) {
            return true
        } else {
            SnackbarHelper
                .createSimpleSnackbar(this.binding.fragmentTrailPoiInfoEditViewMessage, this.binding.fragmentTrailPoiInfoEditBottomSheetAddPhoto, R.string.message_text_fields_empty)
                .show()
        }
        return false
    }

    private fun checkTextFieldsAreNotEmpty(): Boolean {
        return TextFieldHelper.checkTextFieldIsNotEmpty(this.binding.fragmentTrailPoiInfoEditTextFieldName, this.binding.fragmentTrailPoiInfoEditTextFieldLayoutName)
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_poi_info_edit

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_trail_poi_info_edit_bottom_sheet_add_photo

    @Suppress("UNUSED_PARAMETER")
    fun onDeletePhotoButtonClick(view:View) {
        deletePhoto()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddPhotoButtonClick(view:View) {
        expandAddPhotoBottomSheet()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMetricsSliderValueChanged(view: CircularSlider, value: Int) {
        setValue(value)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onResetMetricsButtonClick(view: View) {
        setValue(null)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSelectPhotoButtonClick(view:View) {
        requestWritePermission()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTakePhotoButtonClick(view:View) {
        requestWriteAndCameraPermission()
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String) {
        this.trailViewModel?.updateImagePathToUpload(true, filePath)
        this.trailViewModel?.trailPointOfInterest?.value?.data?.photoUrl = filePath
        this.trailViewModel?.notifyDataChanged()
    }

    override fun deletePhoto() {
        if (!this.trailViewModel?.trailPointOfInterest?.value?.data?.photoUrl.isNullOrEmpty()) {
            this.trailViewModel?.updateImagePathToDelete(this.trailViewModel.trailPointOfInterest.value?.data?.photoUrl!!)
            this.trailViewModel?.trailPointOfInterest?.value?.data?.photoUrl = null
            this.trailViewModel?.notifyDataChanged()
        }
    }
}
