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
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel

/*************************************************************************************************
 * Allows to edit information about a trailPointOfInterest
 * @param trailViewModel : the trail data
 ************************************************************************************************/

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
        observeSaveRequestSuccess()
        observeRequestFailure()
    }

    private fun initializeData() {
        this.binding.trailPOIInfoEditFragment = this
        this.binding.trailViewModel = this.trailViewModel
        this.currentValueFormatter.value = ValueFormatters.AltitudeValueFormatter()
        this.currentMaxValue.value = VALUE_MAX_ALTITUDE
    }

    private fun observeTrail() {
        this.trailViewModel?.trail?.observe(this) { trail ->
            if (trail != null) {
                this.trailPointOfInterestPosition?.let { position ->
                    this.trailViewModel.watchPointOfInterest(position)
                }
            }
        }
    }

    private fun observeTrailPOI() {
        this.trailViewModel?.trailPointOfInterest?.observe(this) { trailPOI ->
            if (trailPOI != null) {
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

    private fun setValue(value: Int?) {
        this.trailViewModel?.trailPointOfInterest?.value?.elevation = value
        this.trailViewModel?.trailPointOfInterest?.value = this.trailViewModel?.trailPointOfInterest?.value
    }

    override fun saveData() {
        if (checkDataIsValid()) {
            if (this.trailViewModel?.trailPointOfInterest?.value != null) {
                this.trailViewModel.trailPointOfInterest.value?.name = this.binding.fragmentTrailPoiInfoEditTextFieldName.text.toString()
                this.trailViewModel.trailPointOfInterest.value?.description = this.binding.fragmentTrailPoiInfoEditTextFieldDescription.text.toString()
                this.baseActivity?.showProgressDialog()
                this.trailViewModel.saveTrailInDatabase(true)
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
        this.trailViewModel?.trailPointOfInterest?.value?.photoUrl=filePath
        this.trailViewModel?.notifyDataChanged()
    }

    override fun deletePhoto() {
        if(!this.trailViewModel?.trailPointOfInterest?.value?.photoUrl.isNullOrEmpty()) {
            this.trailViewModel?.updateImagePathToDelete(this.trailViewModel.trailPointOfInterest.value?.photoUrl!!)
            this.trailViewModel?.trailPointOfInterest?.value?.photoUrl = null
            this.trailViewModel?.notifyDataChanged()
        }
    }
}
