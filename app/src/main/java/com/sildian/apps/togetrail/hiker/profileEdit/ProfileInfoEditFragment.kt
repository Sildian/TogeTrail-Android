package com.sildian.apps.togetrail.hiker.profileEdit

import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.uiHelpers.PickerHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.location.model.core.Location
import kotlinx.android.synthetic.main.fragment_profile_info_edit.view.*

/*************************************************************************************************
 * Lets the user edit its profile's information
 * @param hiker : the hiker
 ************************************************************************************************/

class ProfileInfoEditFragment(private val hiker: Hiker?=null) : BaseImagePickerFragment()
{

    /**********************************UI component**********************************************/

    private val photoImageView by lazy {layout.fragment_profile_info_edit_image_view_photo}
    private val addPhotoButton by lazy {layout.fragment_profile_info_edit_button_add_photo}
    private val nameTextFieldLayout by lazy {layout.fragment_profile_info_edit_text_field_layout_name}
    private val nameTextField by lazy {layout.fragment_profile_info_edit_text_field_name}
    private val birthdayTextFieldDropdown by lazy {layout.fragment_profile_info_edit_text_field_dropdown_birthday}
    private val liveLocationTextField by lazy {layout.fragment_profile_info_edit_text_field_live_location}
    private val descriptionTextField by lazy {layout.fragment_profile_info_edit_text_field_description}
    private val selectPhotoButton by lazy {layout.fragment_profile_info_edit_button_select_photo}
    private val takePhotoButton by lazy {layout.fragment_profile_info_edit_button_take_photo}

    /*****************************************Data***********************************************/

    override fun updateData(data:Any?) {
        if(data is Location){
            this.hiker?.liveLocation=data
            updateLiveLocationTextField()
        }
    }

    override fun saveData() {
        if(checkDataIsValid()) {
            this.hiker?.name = this.nameTextField.text.toString()
            this.hiker?.birthday =
                if (!this.birthdayTextFieldDropdown.text.isNullOrEmpty())
                    DateUtilities.getDateFromString(this.birthdayTextFieldDropdown.text.toString())
                else null
            this.hiker?.description = this.descriptionTextField.text.toString()
            (activity as ProfileEditActivity).saveHiker()
        }
    }

    override fun checkDataIsValid():Boolean =
        TextFieldHelper.checkTextFieldIsNotEmpty(this.nameTextField, this.nameTextFieldLayout)

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile_info_edit

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_profile_info_edit_bottom_sheet_add_photo

    override fun initializeUI(){
        initializeAddPhotoButton()
        initializeSelectPhotoButton()
        initializeTakePhotoButton()
        refreshUI()
    }

    override fun refreshUI(){
        updatePhoto()
        updateNameTextField()
        updateBirthdayTextFieldDropdown()
        updateLiveLocationTextField()
        updateDescriptionTextField()
    }

    private fun updatePhoto(){
        Glide.with(this)
            .load(this.hiker?.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_person_black)
            .into(this.photoImageView)
    }

    private fun initializeAddPhotoButton(){
        this.addPhotoButton.setOnClickListener {
            expandAddPhotoBottomSheet()
        }
    }

    private fun updateNameTextField(){
        this.nameTextField.setText(this.hiker?.name)
    }

    private fun updateBirthdayTextFieldDropdown(){
        PickerHelper.populateEditTextWithDatePicker(
            this.birthdayTextFieldDropdown, activity as AppCompatActivity, this.hiker?.birthday)
    }

    private fun updateLiveLocationTextField(){
        this.liveLocationTextField.setText(this.hiker?.liveLocation?.fullAddress)
        this.liveLocationTextField.setOnClickListener {
            (activity as ProfileEditActivity).searchLocation()
        }
    }

    private fun updateDescriptionTextField(){
        this.descriptionTextField.setText(this.hiker?.description)
    }

    private fun initializeSelectPhotoButton(){
        this.selectPhotoButton.setOnClickListener {
            requestWritePermission()
        }
    }

    private fun initializeTakePhotoButton(){
        this.takePhotoButton.setOnClickListener {
            requestWriteAndCameraPermission()
        }
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String){
        (activity as ProfileEditActivity)
            .updateImagePathToUploadIntoDatabase(filePath)
        this.hiker?.photoUrl=filePath
        updatePhoto()
    }
}
