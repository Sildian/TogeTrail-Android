package com.sildian.apps.togetrail.trail.infoEdit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sdsmdg.harjot.crollerTest.Croller

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.SaveDataFlow
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import kotlinx.android.synthetic.main.fragment_trail_poi_info_edit.view.*
import pl.aprilapps.easyphotopicker.*

/*************************************************************************************************
 * Allows to edit information about a trailPointOfInterest
 * @param trailPointOfInterest : the related trailPointOfInterest
 ************************************************************************************************/

class TrailPOIInfoEditFragment(val trailPointOfInterest: TrailPointOfInterest?=null) :
    Fragment(),
    SaveDataFlow,
    Croller.onProgressChangedListener
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

        /**Values max**/
        private const val VALUE_MAX_ALTITUDE=4000       //Max value for an altitude (in meters)
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val nameTextField by lazy {layout.fragment_trail_poi_info_edit_text_field_name}
    private val photoText by lazy {layout.fragment_trail_poi_info_edit_text_photo}
    private val photoImageView by lazy {layout.fragment_trail_poi_info_edit_image_view_photo}
    private val deletePhotoButton by lazy {layout.fragment_trail_poi_info_edit_button_delete_photo}
    private val addPhotoButton by lazy {layout.fragment_trail_poi_info_edit_button_add_photo}
    private val takePhotoButton by lazy {layout.fragment_trail_poi_info_edit_button_take_photo}
    private val metricsCroller by lazy {layout.fragment_trail_poi_info_edit_croller_metrics}
    private val elevationText by lazy {layout.fragment_trail_poi_info_edit_text_elevation}
    private val descriptionTextField by lazy {layout.fragment_trail_poi_info_edit_text_field_description}

    /**********************************Pictures support******************************************/

    //EasyImage support allowing to pick pictures on the device
    private lateinit var easyImage: EasyImage

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout= inflater.inflate(R.layout.fragment_trail_poi_info_edit, container, false)
        initializeEasyImage()
        initializeAllUIComponents()
        return this.layout
    }

    /*********************************Data monitoring********************************************/

    override fun saveData() {
        this.trailPointOfInterest?.name=this.nameTextField.text.toString()
        this.trailPointOfInterest?.description=this.descriptionTextField.text.toString()
        (activity as TrailInfoEditActivity).updateTrailPoiAndSave(this.trailPointOfInterest!!)
    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeNameTextField()
        initializeDeletePhotoButton()
        initializeAddPhotoButton()
        initializeTakePhotoButton()
        initializeMetricsCroller()
        initializeElevationText()
        initializeDescriptionTextField()
        updatePhoto()
    }

    private fun initializeNameTextField(){
        this.nameTextField.setText(this.trailPointOfInterest?.name)
    }

    private fun initializeDeletePhotoButton(){
        this.deletePhotoButton.setOnClickListener {
            deletePhoto()
        }
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

    private fun initializeMetricsCroller(){
        val elevation=this.trailPointOfInterest?.elevation
        updateCroller(elevation)
        this.metricsCroller.setOnProgressChangedListener(this)
    }

    private fun initializeElevationText(){
        val elevation=this.trailPointOfInterest?.elevation
        updateElevation(elevation)
    }

    private fun initializeDescriptionTextField(){
        this.descriptionTextField.setText(this.trailPointOfInterest?.description)
    }

    private fun updatePhoto(){
        Glide.with(context!!)
            .load(this.trailPointOfInterest?.photoUrl)
            .apply(RequestOptions.fitCenterTransform())
            .placeholder(R.drawable.ic_trail_black)
            .into(this.photoImageView)
        updatePhotoVisibility()
    }

    private fun updatePhotoVisibility(){

        /*If no photo is available, shows a text to notify the user*/

        if(this.trailPointOfInterest?.photoUrl.isNullOrEmpty()){
            this.photoText.visibility=View.VISIBLE
            this.photoImageView.visibility=View.INVISIBLE
            this.deletePhotoButton.visibility=View.INVISIBLE
        }

        /*Else shows the image*/

        else{
            this.photoText.visibility=View.INVISIBLE
            this.photoImageView.visibility=View.VISIBLE
            this.deletePhotoButton.visibility=View.VISIBLE
        }
    }

    /*****************************Metrics monitoring with croller*********************************/

    override fun onProgressChanged(progress: Int) {
        updateElevation(progress)
    }

    private fun updateCroller(currentValue:Int?){
        this.metricsCroller.max= VALUE_MAX_ALTITUDE
        this.metricsCroller.progress=currentValue?:0
    }

    private fun updateElevation(elevation:Int?){
        this.trailPointOfInterest?.elevation=elevation
        val elevationToDisplay=MetricsHelper.displayElevation(
            context!!, elevation, true, true)
        val elevationToDisplayInCroller=MetricsHelper.displayElevation(
            context!!, elevation, false, false)
        this.elevationText.text=elevationToDisplay
        this.metricsCroller.label = elevationToDisplayInCroller
    }

    /*******************************Photos monitoring********************************************/

    private fun initializeEasyImage(){
        this.easyImage= EasyImage.Builder(context!!)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false)
            .build()
    }

    private fun addPhoto(filePath:String){
        (activity as TrailInfoEditActivity)
            .updateImagePathToUploadIntoDatabase(filePath)
        this.trailPointOfInterest?.photoUrl=filePath
        updatePhoto()
    }

    private fun deletePhoto(){
        if(!this.trailPointOfInterest?.photoUrl.isNullOrEmpty()) {
            (activity as TrailInfoEditActivity)
                .updateImagePathToDeleteFromDatabase(this.trailPointOfInterest?.photoUrl!!)
            this.trailPointOfInterest.photoUrl = null
            updatePhoto()
        }
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
                    arrayOf(
                        KEY_BUNDLE_PERMISSION_WRITE,
                        KEY_BUNDLE_PERMISSION_CAMERA
                    ),
                    KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA
                )
            }
        }else{

            /*If SDK <23 or permission already granted, directly proceeds the action*/

            startTakePhoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            KEY_REQUEST_PERMISSION_WRITE->if(grantResults.isNotEmpty()){
                when(grantResults[0]) {
                    PackageManager.PERMISSION_GRANTED -> {
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '${KEY_BUNDLE_PERMISSION_WRITE}' granted"
                        )
                        startAddPhoto()
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        //TODO handle
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '${KEY_BUNDLE_PERMISSION_WRITE}' denied"
                        )
                    }
                }
            }
            KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA ->if(grantResults.isNotEmpty()){
                when(grantResults[0]){
                    PackageManager.PERMISSION_GRANTED -> {
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '${KEY_BUNDLE_PERMISSION_WRITE}' granted")
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '${KEY_BUNDLE_PERMISSION_CAMERA}' granted")
                        startTakePhoto()
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        //TODO handle
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '${KEY_BUNDLE_PERMISSION_WRITE}' denied")
                        Log.d(
                            TAG_PERMISSION,
                            "Permission '${KEY_BUNDLE_PERMISSION_CAMERA}' denied")
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
