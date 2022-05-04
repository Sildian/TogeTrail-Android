package com.sildian.apps.togetrail.hiker.ui.profileEdit

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.uiHelpers.PickerHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.databinding.FragmentProfileInfoEditBinding
import com.sildian.apps.togetrail.hiker.data.dataRequests.HikerSaveDataRequest
import com.sildian.apps.togetrail.hiker.data.viewModels.HikerViewModel
import com.sildian.apps.togetrail.location.data.core.Location
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Lets the user edit its profile's information
 * @param hikerId : the hiker's id
 ************************************************************************************************/

@AndroidEntryPoint
class ProfileInfoEditFragment(private val hikerId: String?=null) :
    BaseImagePickerFragment<FragmentProfileInfoEditBinding>()
{

    /*****************************************Data***********************************************/

    private val hikerViewModel: HikerViewModel by viewModels()

    /***********************************Life cycle***********************************************/

    override fun onDestroy() {
        this.hikerViewModel.clearImagePaths()
        super.onDestroy()
    }

    /******************************Data monitoring***********************************************/

    override fun loadData() {
        initializeData()
        observeHiker()
        observeDataRequestState()
        loadHiker()
    }

    private fun initializeData() {
        this.binding.profileInfoEditFragment = this
        this.binding.hikerViewModel = this.hikerViewModel
    }

    private fun observeHiker() {
        this.hikerViewModel.data.observe(this) { hikerData ->
            hikerData?.error?.let { e ->
                onQueryError(e)
            } ?:
            hikerData?.data?.let { hiker ->
                refreshUI()
            }
        }
    }

    private fun observeDataRequestState() {
        this.hikerViewModel.dataRequestState.observe(this) { dataRequestState ->
            if (dataRequestState?.dataRequest is HikerSaveDataRequest) {
                dataRequestState.error?.let { e ->
                    onQueryError(e)
                } ?: onQuerySuccess()
            }
        }
    }

    private fun loadHiker() {
        this.hikerId?.let { hikerId ->
            this.hikerViewModel.loadHiker(hikerId)
        }
    }

    override fun updateData(data:Any?) {
        if (data is Location) {
            this.hikerViewModel.data.value?.data?.liveLocation = data
            this.binding.fragmentProfileInfoEditTextFieldLiveLocation.setText(data.fullAddress)
        }
    }

    override fun saveData() {
        if(checkDataIsValid()) {
            this.hikerViewModel.data.value?.data?.name = this.binding.fragmentProfileInfoEditTextFieldName.text.toString()
            this.hikerViewModel.data.value?.data?.birthday =
                if (!this.binding.fragmentProfileInfoEditTextFieldDropdownBirthday.text.isNullOrEmpty())
                    DateUtilities.getDateFromString(this.binding.fragmentProfileInfoEditTextFieldDropdownBirthday.text.toString())
                else null
            this.hikerViewModel.data.value?.data?.description = this.binding.fragmentProfileInfoEditTextFieldDescription.text.toString()
            this.baseActivity?.showProgressDialog()
            this.hikerViewModel.saveHiker()
        }
    }

    override fun checkDataIsValid():Boolean =
        TextFieldHelper.checkTextFieldIsNotEmpty(this.binding.fragmentProfileInfoEditTextFieldName, this.binding.fragmentProfileInfoEditTextFieldLayoutName)

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile_info_edit

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_profile_info_edit_bottom_sheet_add_photo

    override fun refreshUI() {
        updateBirthdayTextFieldDropdown()
    }

    private fun updateBirthdayTextFieldDropdown() {
        PickerHelper.populateEditTextWithDatePicker(
            this.binding.fragmentProfileInfoEditTextFieldDropdownBirthday, activity as AppCompatActivity, this.hikerViewModel.data.value?.data?.birthday)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddPhotoButtonClick(view: View) {
        expandAddPhotoBottomSheet()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSelectPhotoButtonClick(view: View) {
        requestWritePermission()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTakePhotoButtonClick(view: View) {
        requestWriteAndCameraPermission()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLiveLocationTextFieldClick(view:View) {
        (activity as ProfileEditActivity).searchLocation()
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String) {
        this.hikerViewModel.updateImagePathToUpload(filePath)
        this.hikerViewModel.data.value?.data?.photoUrl = filePath
        this.hikerViewModel.notifyDataChanged()
    }
}
