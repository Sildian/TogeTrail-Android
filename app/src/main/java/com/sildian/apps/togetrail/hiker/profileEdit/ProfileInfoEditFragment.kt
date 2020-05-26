package com.sildian.apps.togetrail.hiker.profileEdit

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.uiHelpers.PickerHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.common.viewModels.ViewModelFactory
import com.sildian.apps.togetrail.databinding.FragmentProfileInfoEditBinding
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel
import com.sildian.apps.togetrail.location.model.core.Location
import kotlinx.android.synthetic.main.fragment_profile_info_edit.view.*

/*************************************************************************************************
 * Lets the user edit its profile's information
 * @param hiker : the hiker
 ************************************************************************************************/

class ProfileInfoEditFragment(private val hiker: Hiker?=null) : BaseImagePickerFragment()
{

    /*****************************************Data***********************************************/

    /*TODO private*/ lateinit var hikerViewModel: HikerViewModel

    /**********************************UI component**********************************************/

    private val photoImageView by lazy {layout.fragment_profile_info_edit_image_view_photo}
    private val nameTextFieldLayout by lazy {layout.fragment_profile_info_edit_text_field_layout_name}
    private val nameTextField by lazy {layout.fragment_profile_info_edit_text_field_name}
    private val birthdayTextFieldDropdown by lazy {layout.fragment_profile_info_edit_text_field_dropdown_birthday}
    private val liveLocationTextField by lazy {layout.fragment_profile_info_edit_text_field_live_location}
    private val descriptionTextField by lazy {layout.fragment_profile_info_edit_text_field_description}

    /******************************Data monitoring***********************************************/

    override fun loadData() {
        this.hikerViewModel= ViewModelProviders
            .of(this, ViewModelFactory)
            .get(HikerViewModel::class.java)
        (this.binding as FragmentProfileInfoEditBinding).profileInfoEditFragment=this
        (this.binding as FragmentProfileInfoEditBinding).hikerViewModel=this.hikerViewModel
        this.hiker?.id?.let { hikerId ->
            this.hikerViewModel.loadHikerFromDatabase(hikerId, null, this::handleQueryError)
        }
    }

    override fun updateData(data:Any?) {
        if(data is Location){
            this.hikerViewModel.hiker?.liveLocation=data
        }
    }

    override fun saveData() {
        if(checkDataIsValid()) {
            this.hikerViewModel.hiker?.name = this.nameTextField.text.toString()
            this.hikerViewModel.hiker?.birthday =
                if (!this.birthdayTextFieldDropdown.text.isNullOrEmpty())
                    DateUtilities.getDateFromString(this.birthdayTextFieldDropdown.text.toString())
                else null
            this.hikerViewModel.hiker?.description = this.descriptionTextField.text.toString()
            (activity as ProfileEditActivity).saveHiker()
        }
    }

    override fun checkDataIsValid():Boolean =
        TextFieldHelper.checkTextFieldIsNotEmpty(this.nameTextField, this.nameTextFieldLayout)

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile_info_edit

    override fun useDataBinding(): Boolean = true

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_profile_info_edit_bottom_sheet_add_photo

    override fun initializeUI(){
        this.hikerViewModel.addOnPropertyChangedCallback(object:Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                refreshUI()
            }
        })
    }

    override fun refreshUI(){
        updatePhoto()
        updateBirthdayTextFieldDropdown()
    }

    private fun updatePhoto(){
        Glide.with(this)
            .load(this.hikerViewModel.hiker?.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_person_black)
            .into(this.photoImageView)
    }

    private fun updateBirthdayTextFieldDropdown(){
        PickerHelper.populateEditTextWithDatePicker(
            this.birthdayTextFieldDropdown, activity as AppCompatActivity, this.hikerViewModel.hiker?.birthday)
    }

    fun onAddPhotoButtonClick(view: View){
        expandAddPhotoBottomSheet()
    }

    fun onSelectPhotoButtonClick(view: View){
        requestWritePermission()
    }

    fun onTakePhotoButtonClick(view: View){
        requestWriteAndCameraPermission()
    }

    fun onLiveLocationTextFieldClick(view:View){
        (activity as ProfileEditActivity).searchLocation()
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String){
        (activity as ProfileEditActivity)
            .updateImagePathToUploadIntoDatabase(filePath)
        this.hikerViewModel.hiker?.photoUrl=filePath
        updatePhoto()
    }
}
