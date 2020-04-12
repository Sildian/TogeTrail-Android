package com.sildian.apps.togetrail.common.baseControllers

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import pl.aprilapps.easyphotopicker.*

/*************************************************************************************************
 * Base fragment for all fragment aiming to pick images
 ************************************************************************************************/

abstract class BaseImagePickerFragment : BaseDataFlowFragment() {

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

    //EasyImage support allowing to pick pictures on the device
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

    /**Requests Write permission (to pick a photo on the device)**/

    protected fun requestWritePermission(){
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

            startSelectPhoto()
        }
    }

    /**Requests Write and Camera permission (to take a new photo with the device)**/

    protected fun requestWriteAndCameraPermission(){
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

    /**Handles permissions requests results**/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            KEY_REQUEST_PERMISSION_WRITE ->if(grantResults.isNotEmpty()){
                when(grantResults[0]) {
                    PackageManager.PERMISSION_GRANTED -> {
                        Log.d(TAG, "Permission '$KEY_BUNDLE_PERMISSION_WRITE' granted")
                        startSelectPhoto()
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        //TODO handle
                        Log.d(TAG, "Permission '$KEY_BUNDLE_PERMISSION_WRITE' denied")
                    }
                }
            }
            KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA ->if(grantResults.isNotEmpty()){
                when(grantResults[0]){
                    PackageManager.PERMISSION_GRANTED -> {
                        Log.d(TAG, "Permission '$KEY_BUNDLE_PERMISSION_WRITE' granted")
                        Log.d(TAG, "Permission '$KEY_BUNDLE_PERMISSION_CAMERA' granted")
                        startTakePhoto()
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        //TODO handle
                        Log.d(TAG, "Permission '$KEY_BUNDLE_PERMISSION_WRITE' denied")
                        Log.d(TAG, "Permission '$KEY_BUNDLE_PERMISSION_CAMERA' denied")
                    }
                }
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

            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                Log.d(TAG, "Successfully added the new photo")
                for(image in imageFiles){
                    addPhoto(image.file.toURI().path)
                }
            }

            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                super.onImagePickerError(error, source)
                Log.w(TAG, error.message.toString())
                //TODO handle
            }
        })
    }
}
