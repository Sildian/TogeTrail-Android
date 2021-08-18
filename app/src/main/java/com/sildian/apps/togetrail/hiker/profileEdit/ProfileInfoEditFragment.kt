package com.sildian.apps.togetrail.hiker.profileEdit

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.uiHelpers.PickerHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.databinding.FragmentProfileInfoEditBinding
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel
import com.sildian.apps.togetrail.location.model.core.Location

/*************************************************************************************************
 * Lets the user edit its profile's information
 * @param hikerId : the hiker's id
 ************************************************************************************************/

class ProfileInfoEditFragment(private val hikerId: String?=null) :
    BaseImagePickerFragment<FragmentProfileInfoEditBinding>()
{

    /*****************************************Data***********************************************/

    private lateinit var hikerViewModel: HikerViewModel

    /***********************************Life cycle***********************************************/

    override fun onDestroy() {
        this.hikerViewModel.clearImagePaths()
        super.onDestroy()
    }

    /******************************Data monitoring***********************************************/

    override fun loadData() {
        initializeData()
        observeHiker()
        observeSaveRequestSuccess()
        observeRequestFailure()
        loadHiker()
    }

    private fun initializeData() {
        this.hikerViewModel= ViewModelProviders
            .of(this, ViewModelFactory)
            .get(HikerViewModel::class.java)
        this.binding.profileInfoEditFragment = this
        this.binding.hikerViewModel = this.hikerViewModel
    }

    private fun observeHiker() {
        this.hikerViewModel.hiker.observe(this) { hiker ->
            if (hiker != null) {
                refreshUI()
            }
        }
    }

    private fun observeSaveRequestSuccess() {
        this.hikerViewModel.saveRequestSuccess.observe(this) { success ->
            if (success) {
                onSaveSuccess()
            }
        }
    }

    private fun observeRequestFailure() {
        this.hikerViewModel.requestFailure.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    private fun loadHiker() {
        this.hikerId?.let { hikerId ->
            this.hikerViewModel.loadHikerFromDatabase(hikerId)
        }
    }

    override fun updateData(data:Any?) {
        if(data is Location){
            this.hikerViewModel.hiker.value?.liveLocation=data
            this.binding.fragmentProfileInfoEditTextFieldLiveLocation.setText(data.fullAddress)
        }
    }

    override fun saveData() {
        if(checkDataIsValid()) {
            this.hikerViewModel.hiker.value?.name = this.binding.fragmentProfileInfoEditTextFieldName.text.toString()
            this.hikerViewModel.hiker.value?.birthday =
                if (!this.binding.fragmentProfileInfoEditTextFieldDropdownBirthday.text.isNullOrEmpty())
                    DateUtilities.getDateFromString(this.binding.fragmentProfileInfoEditTextFieldDropdownBirthday.text.toString())
                else null
            this.hikerViewModel.hiker.value?.description = this.binding.fragmentProfileInfoEditTextFieldDescription.text.toString()
            this.baseActivity?.showProgressDialog()
            this.hikerViewModel.saveHikerInDatabase()
        }
    }

    override fun checkDataIsValid():Boolean =
        TextFieldHelper.checkTextFieldIsNotEmpty(this.binding.fragmentProfileInfoEditTextFieldName, this.binding.fragmentProfileInfoEditTextFieldLayoutName)

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile_info_edit

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_profile_info_edit_bottom_sheet_add_photo

    override fun refreshUI(){
        updateBirthdayTextFieldDropdown()
    }

    private fun updateBirthdayTextFieldDropdown(){
        PickerHelper.populateEditTextWithDatePicker(
            this.binding.fragmentProfileInfoEditTextFieldDropdownBirthday, activity as AppCompatActivity, this.hikerViewModel.hiker.value?.birthday)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddPhotoButtonClick(view: View){
        expandAddPhotoBottomSheet()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSelectPhotoButtonClick(view: View){
        requestWritePermission()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTakePhotoButtonClick(view: View){
        requestWriteAndCameraPermission()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLiveLocationTextFieldClick(view:View){
        (activity as ProfileEditActivity).searchLocation()
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String){
        this.hikerViewModel.updateImagePathToUpload(filePath)
        this.hikerViewModel.hiker.value?.photoUrl=filePath
        this.hikerViewModel.notifyDataChanged()
    }
}
