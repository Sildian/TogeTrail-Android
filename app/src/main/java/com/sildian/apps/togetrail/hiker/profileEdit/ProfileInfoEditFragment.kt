package com.sildian.apps.togetrail.hiker.profileEdit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.model.Location
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.uiHelpers.PickerHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import kotlinx.android.synthetic.main.fragment_profile_info_edit.view.*
import pl.aprilapps.easyphotopicker.*

/*************************************************************************************************
 * Lets the user edit its profile's information
 * @param hiker : the current user
 ************************************************************************************************/

class ProfileInfoEditFragment(val hiker: Hiker?=null) : BaseDataFlowFragment()
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG_FRAGMENT="TAG_FRAGMENT"
        private const val TAG_PERMISSION="TAG_PERMISSION"
        private const val TAG_PHOTO="TAG_PHOTO"

        /**Request keys for permissions**/
        private const val KEY_REQUEST_PERMISSION_WRITE=2001
        private const val KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA=2002

        /**Bundle keys for permissions**/
        private const val KEY_BUNDLE_PERMISSION_WRITE= Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val KEY_BUNDLE_PERMISSION_CAMERA= Manifest.permission.CAMERA
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val photoImageView by lazy {layout.fragment_profile_info_edit_image_view_photo}
    private val addPhotoButton by lazy {layout.fragment_profile_info_edit_button_add_photo}
    private val takePhotoButton by lazy {layout.fragment_profile_info_edit_button_take_photo}
    private val nameTextField by lazy {layout.fragment_profile_info_edit_text_field_name}
    private val birthdayTextFieldDropdown by lazy {layout.fragment_profile_info_edit_text_field_dropdown_birthday}
    private val countryTextField by lazy {layout.fragment_profile_info_edit_text_field_country}
    private val regionTextField by lazy {layout.fragment_profile_info_edit_text_field_region}
    private val townTextField by lazy {layout.fragment_profile_info_edit_text_field_town}
    private val descriptionTextField by lazy {layout.fragment_profile_info_edit_text_field_description}

    /**********************************Pictures support******************************************/

    //EasyImage support allowing to pick pictures on the device
    private lateinit var easyImage: EasyImage

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_profile_info_edit, container, false)
        initializeEasyImage()
        initializeAllUIComponents()
        return this.layout
    }

    /*****************************************Data***********************************************/

    override fun saveData() {
        this.hiker?.name=this.nameTextField.text.toString()
        this.hiker?.birthday=
            if(!this.birthdayTextFieldDropdown.text.isNullOrEmpty())
                DateUtilities.getDateFromString(this.birthdayTextFieldDropdown.text.toString())
            else null
        this.hiker?.liveLocation=
            Location(
                this.countryTextField.text.toString(),
                this.regionTextField.text.toString(),
                this.townTextField.text.toString())
        this.hiker?.description=this.descriptionTextField.text.toString()
        (activity as ProfileEditActivity).saveHiker()
    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeAddPhotoButton()
        initializeTakePhotoButton()
        initializeNameTextField()
        initializeBirthdayTextFieldDropdown()
        initializeCountryTextField()
        initializeRegionTextField()
        initializeTownTextField()
        initializeDescriptionTextField()
        updatePhoto()
    }

    private fun initializeAddPhotoButton(){
        this.addPhotoButton.setOnClickListener {
            requestWritePermission()
        }
    }

    private fun initializeTakePhotoButton(){
        this.takePhotoButton.setOnClickListener {
            requestWriteAndCameraPermission()
        }
    }

    private fun initializeNameTextField(){
        this.nameTextField.setText(this.hiker?.name)
    }

    private fun initializeBirthdayTextFieldDropdown(){
        PickerHelper.populateEditTextWithDatePicker(
            this.birthdayTextFieldDropdown, activity as AppCompatActivity, this.hiker?.birthday)
    }

    private fun initializeCountryTextField(){
        this.countryTextField.setText(this.hiker?.liveLocation?.country)
    }

    private fun initializeRegionTextField(){
        this.regionTextField.setText(this.hiker?.liveLocation?.region)
    }

    private fun initializeTownTextField(){
        this.townTextField.setText(this.hiker?.liveLocation?.town)
    }

    private fun initializeDescriptionTextField(){
        this.descriptionTextField.setText(this.hiker?.description)
    }

    private fun updatePhoto(){
        Glide.with(this)
            .load(this.hiker?.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_user_black)
            .into(this.photoImageView)
    }

    /*******************************Photos monitoring********************************************/

    private fun initializeEasyImage(){
        this.easyImage= EasyImage.Builder(context!!)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false)
            .build()
    }

    private fun addPhoto(filePath:String){
        (activity as ProfileEditActivity)
            .updateImagePathToUploadIntoDatabase(filePath)
        this.hiker?.photoUrl=filePath
        updatePhoto()
    }

    /***********************************Permissions**********************************************/

    private fun requestWritePermission(){
        if(Build.VERSION.SDK_INT>=23
            && activity?.checkSelfPermission(KEY_BUNDLE_PERMISSION_WRITE)!= PackageManager.PERMISSION_GRANTED){

            /*If permission not already granted, requests it*/

            if(shouldShowRequestPermissionRationale(KEY_BUNDLE_PERMISSION_WRITE)){

                //TODO handle

            }else{
                requestPermissions(
                    arrayOf(KEY_BUNDLE_PERMISSION_WRITE), KEY_REQUEST_PERMISSION_WRITE)
            }
        }else{

            /*If SDK <23 or permission already granted, directly proceeds the action*/

            startAddPhoto()
        }
    }

    private fun requestWriteAndCameraPermission(){
        if(Build.VERSION.SDK_INT>=23
            &&(activity?.checkSelfPermission(KEY_BUNDLE_PERMISSION_WRITE)!= PackageManager.PERMISSION_GRANTED
                    ||activity?.checkSelfPermission(KEY_BUNDLE_PERMISSION_CAMERA)!= PackageManager.PERMISSION_GRANTED)){

            /*If permission not already granted, requests it*/

            if(shouldShowRequestPermissionRationale(KEY_BUNDLE_PERMISSION_WRITE)
                || shouldShowRequestPermissionRationale(KEY_BUNDLE_PERMISSION_CAMERA)){

                //TODO handle

            }else{
                requestPermissions(
                    arrayOf(KEY_BUNDLE_PERMISSION_WRITE, KEY_BUNDLE_PERMISSION_CAMERA),
                    KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA)
            }
        }else{

            /*If SDK <23 or permission already granted, directly proceeds the action*/

            startTakePhoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            KEY_REQUEST_PERMISSION_WRITE ->if(grantResults.isNotEmpty()){
                when(grantResults[0]) {
                    PackageManager.PERMISSION_GRANTED -> {
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '$KEY_BUNDLE_PERMISSION_WRITE' granted"
                        )
                        startAddPhoto()
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        //TODO handle
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '$KEY_BUNDLE_PERMISSION_WRITE' denied"
                        )
                    }
                }
            }
            KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA ->if(grantResults.isNotEmpty()){
                when(grantResults[0]){
                    PackageManager.PERMISSION_GRANTED -> {
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '$KEY_BUNDLE_PERMISSION_WRITE' granted")
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '$KEY_BUNDLE_PERMISSION_CAMERA' granted")
                        startTakePhoto()
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        //TODO handle
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '$KEY_BUNDLE_PERMISSION_WRITE' denied")
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '$KEY_BUNDLE_PERMISSION_CAMERA' denied")
                    }
                }
            }
        }
    }

    /***********************************Navigation***********************************************/

    private fun startAddPhoto(){
        this.easyImage.openGallery(this)
    }

    private fun startTakePhoto(){
        this.easyImage.openCameraForImage(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleNewPhotoResult(requestCode, resultCode, data)
    }

    private fun handleNewPhotoResult(requestCode: Int, resultCode: Int, data: Intent?){

        this.easyImage.handleActivityResult(requestCode, resultCode, data, activity!!, object: DefaultCallback(){

            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                Log.d(TAG_PHOTO, "Successfully added new photo")
                for(image in imageFiles){
                    addPhoto(image.file.toURI().path)
                }
            }

            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                super.onImagePickerError(error, source)
                Log.w(TAG_PHOTO, error.message.toString())
                //TODO handle
            }
        })
    }
}
