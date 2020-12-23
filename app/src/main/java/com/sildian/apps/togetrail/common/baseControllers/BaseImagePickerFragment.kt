package com.sildian.apps.togetrail.common.baseControllers

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.permissionsHelpers.PermissionsCallback
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import pl.aprilapps.easyphotopicker.*

/*************************************************************************************************
 * Base fragment for all fragment aiming to pick images
 ************************************************************************************************/

abstract class BaseImagePickerFragment : BaseFragment(), PermissionsCallback {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG="BaseImagePickerFragment"

        /**Request keys for permissions**/
        private const val KEY_REQUEST_PERMISSION_WRITE=2001
        private const val KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA=2002

        /**Bundle keys for permissions**/
        private const val KEY_BUNDLE_PERMISSION_WRITE= Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val KEY_BUNDLE_PERMISSION_CAMERA= Manifest.permission.CAMERA
    }

    /**********************************UI component**********************************************/

    private lateinit var addPhotoBottomSheet: BottomSheetBehavior<View>

    /**********************************Pictures support******************************************/

    private lateinit var easyImage: EasyImage

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initializeEasyImage()
        initializeAddPhotoBottomSheet()
        return this.layout
    }

    /**********************************UI monitoring*********************************************/

    abstract fun getAddPhotoBottomSheetId():Int

    private fun initializeAddPhotoBottomSheet(){
        this.addPhotoBottomSheet=
            BottomSheetBehavior
                .from(this.layout.findViewById(getAddPhotoBottomSheetId()))
        hideAddPhotoBottomSheet()
    }

    fun getAddPhotoBottomSheetState():Int = this.addPhotoBottomSheet.state

    fun hideAddPhotoBottomSheet(){
        this.addPhotoBottomSheet.state=BottomSheetBehavior.STATE_HIDDEN
    }

    fun expandAddPhotoBottomSheet(){
        this.addPhotoBottomSheet.state=BottomSheetBehavior.STATE_EXPANDED
    }

    /*******************************Photos monitoring********************************************/

    private fun initializeEasyImage(){
        this.easyImage= EasyImage.Builder(context!!)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false)
            .build()
    }

    abstract fun addPhoto(filePath:String)

    open fun deletePhoto(){}

    /***********************************Permissions**********************************************/

    protected fun requestWritePermission() {
        baseActivity?.requestPermissions(
            KEY_REQUEST_PERMISSION_WRITE,
            arrayOf(KEY_BUNDLE_PERMISSION_WRITE),
            this,
            R.string.message_permission_requested_message_write
        )
    }

    protected fun requestWriteAndCameraPermission(){
        baseActivity?.requestPermissions(
            KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA,
            arrayOf(KEY_BUNDLE_PERMISSION_WRITE, KEY_BUNDLE_PERMISSION_CAMERA),
            this,
            R.string.message_permission_requested_message_write_and_camera
        )
    }

    override fun onPermissionsGranted(permissionsRequestCode: Int) {
        hideAddPhotoBottomSheet()
        when (permissionsRequestCode) {
            KEY_REQUEST_PERMISSION_WRITE -> startSelectPhoto()
            KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA -> startTakePhoto()
        }
    }

    override fun onPermissionsDenied(permissionsRequestCode: Int) {

        hideAddPhotoBottomSheet()
        baseActivity?.let { baseActivity ->

            when (permissionsRequestCode) {

                KEY_REQUEST_PERMISSION_WRITE ->
                    DialogHelper.createInfoDialog(
                        baseActivity,
                        R.string.message_permission_denied_title,
                        R.string.message_permission_denied_message_write
                    ).show()

                KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA ->
                    DialogHelper.createInfoDialog(
                        baseActivity,
                        R.string.message_permission_denied_title,
                        R.string.message_permission_denied_message_write_and_camera
                    ).show()
            }
        }
    }

    /***********************************Navigation***********************************************/

    private fun startSelectPhoto(){
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

            /*If success, adds the photo*/

            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                Log.d(TAG, "Successfully added the new photo")
                for(image in imageFiles){
                    addPhoto(image.file.toURI().path)
                }
            }

            /*If error, shows a message*/

            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                super.onImagePickerError(error, source)
                Log.w(TAG, error.message.toString())
                DialogHelper.createInfoDialog(
                    context!!,
                    R.string.message_file_failure,
                    R.string.message_file_failure_image
                ).show()
            }
        })
    }
}
