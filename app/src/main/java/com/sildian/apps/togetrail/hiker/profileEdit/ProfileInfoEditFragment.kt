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
import kotlinx.android.synthetic.main.fragment_profile_info_edit.view.*

/*************************************************************************************************
 * Lets the user edit its profile's information
 * @param hikerId : the hiker's id
 ************************************************************************************************/

class ProfileInfoEditFragment(private val hikerId: String?=null) : BaseImagePickerFragment()
{

    /*****************************************Data***********************************************/

    private lateinit var hikerViewModel: HikerViewModel

    /**********************************UI component**********************************************/

    private val nameTextFieldLayout by lazy {layout.fragment_profile_info_edit_text_field_layout_name}
    private val nameTextField by lazy {layout.fragment_profile_info_edit_text_field_name}
    private val birthdayTextFieldDropdown by lazy {layout.fragment_profile_info_edit_text_field_dropdown_birthday}
    private val liveLocationTextField by lazy {layout.fragment_profile_info_edit_text_field_live_location}
    private val descriptionTextField by lazy {layout.fragment_profile_info_edit_text_field_description}

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
        this.binding.lifecycleOwner = this
        (this.binding as FragmentProfileInfoEditBinding).profileInfoEditFragment = this
        (this.binding as FragmentProfileInfoEditBinding).hikerViewModel = this.hikerViewModel
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
            this.liveLocationTextField.setText(data.fullAddress)
        }
    }

    override fun saveData() {
        if(checkDataIsValid()) {
            this.hikerViewModel.hiker.value?.name = this.nameTextField.text.toString()
            this.hikerViewModel.hiker.value?.birthday =
                if (!this.birthdayTextFieldDropdown.text.isNullOrEmpty())
                    DateUtilities.getDateFromString(this.birthdayTextFieldDropdown.text.toString())
                else null
            this.hikerViewModel.hiker.value?.description = this.descriptionTextField.text.toString()
            this.baseActivity?.showProgressDialog()
            this.hikerViewModel.saveHikerInDatabase()
        }
    }

    override fun checkDataIsValid():Boolean =
        TextFieldHelper.checkTextFieldIsNotEmpty(this.nameTextField, this.nameTextFieldLayout)

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile_info_edit

    override fun useDataBinding(): Boolean = true

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_profile_info_edit_bottom_sheet_add_photo

    override fun refreshUI(){
        updateBirthdayTextFieldDropdown()
    }

    private fun updateBirthdayTextFieldDropdown(){
        PickerHelper.populateEditTextWithDatePicker(
            this.birthdayTextFieldDropdown, activity as AppCompatActivity, this.hikerViewModel.hiker.value?.birthday)
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
