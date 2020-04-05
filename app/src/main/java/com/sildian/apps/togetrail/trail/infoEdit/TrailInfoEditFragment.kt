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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sdsmdg.harjot.crollerTest.Croller
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.uiHelpers.DropdownMenuHelper
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import kotlinx.android.synthetic.main.fragment_trail_info_edit.view.*
import pl.aprilapps.easyphotopicker.*

/*************************************************************************************************
 * Allows to edit information about a trail
 * @param trail : the related trail
 ************************************************************************************************/

class TrailInfoEditFragment(private val trail: Trail?=null) :
    BaseDataFlowFragment(),
    Croller.onProgressChangedListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG="TrailInfoEditFragment"

        /**Request keys for permissions**/
        private const val KEY_REQUEST_PERMISSION_WRITE=2001
        private const val KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA=2002

        /**Bundle keys for permissions**/
        private const val KEY_BUNDLE_PERMISSION_WRITE= Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val KEY_BUNDLE_PERMISSION_CAMERA= Manifest.permission.CAMERA

        /**Metrics to set with the croller**/
        private const val METRIC_DURATION=0
        private const val METRIC_ASCENT=1
        private const val METRIC_DESCENT=2
        private const val METRIC_DISTANCE=3
        private const val METRIC_MAX_ELEVATION=4
        private const val METRIC_MIN_ELEVATION=5

        /**Values max**/
        private const val VALUE_MAX_DURATION=720        //Max value for a duration (in minutes)
        private const val VALUE_MAX_DISTANCE=30000      //Max value for a distance (in meters)
        private const val VALUE_MAX_ALTITUDE=4000       //Max value for an altitude (in meters)
    }

    /***************************************Data*************************************************/

    private var currentMetricToSet= METRIC_DURATION     //The current metric to set with the croller

    /**********************************UI component**********************************************/

    private val nameTextField by lazy {layout.fragment_trail_info_edit_text_field_name}
    private val photoText by lazy {layout.fragment_trail_info_edit_text_photo}
    private val photoImageView by lazy {layout.fragment_trail_info_edit_image_view_photo}
    private val deletePhotoButton by lazy {layout.fragment_trail_info_edit_button_delete_photo}
    private val addPhotoButton by lazy {layout.fragment_trail_info_edit_button_add_photo}
    private val takePhotoButton by lazy {layout.fragment_trail_info_edit_button_take_photo}
    private val levelTextFieldDropDown by lazy {layout.fragment_trail_info_edit_text_field_dropdown_level}
    private val metricsCroller by lazy {layout.fragment_trail_info_edit_croller_metrics}
    private val durationText by lazy {layout.fragment_trail_info_edit_text_duration}
    private val ascentText by lazy {layout.fragment_trail_info_edit_text_ascent}
    private val descentText by lazy {layout.fragment_trail_info_edit_text_descent}
    private val distanceText by lazy {layout.fragment_trail_info_edit_text_distance}
    private val maxElevationText by lazy {layout.fragment_trail_info_edit_text_max_elevation}
    private val minElevationText by lazy {layout.fragment_trail_info_edit_text_min_elevation}
    private val locationTextField by lazy {layout.fragment_trail_info_edit_text_field_location}
    private val descriptionTextField by lazy {layout.fragment_trail_info_edit_text_field_description}

    private val metricsTexts by lazy {
        arrayOf(
            this.durationText,
            this.ascentText,
            this.descentText,
            this.distanceText,
            this.maxElevationText,
            this.minElevationText
        )
    }

    /**********************************Pictures support******************************************/

    //EasyImage support allowing to pick pictures on the device
    private lateinit var easyImage: EasyImage

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initializeEasyImage()
        return this.layout
    }

    /*********************************Data monitoring********************************************/

    override fun updateData(data:Any?) {
        if(data is Location){
            this.trail?.location=data
            updateLocationTextField()
        }
    }

    override fun saveData() {
        this.trail?.name=this.nameTextField.text.toString()
        this.trail?.level=
            TrailLevel.fromValue(this.levelTextFieldDropDown.tag.toString().toInt()+1)
        this.trail?.description=this.descriptionTextField.text.toString()
        this.trail?.autoPopulatePosition()
        (activity as TrailInfoEditActivity).saveTrail()
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_info_edit

    override fun initializeUI() {
        initializeDeletePhotoButton()
        initializeAddPhotoButton()
        initializeTakePhotoButton()
        initializeMetricsCroller()
        initializeDurationText()
        initializeAscentText()
        initializeDescentText()
        initializeDistanceText()
        initializeMaxElevationText()
        initializeMinElevationText()
        refreshUI()
    }

    override fun refreshUI() {
        updateNameTextField()
        updateLevelTextFieldDropDown()
        updateCurrentMetricToSet(METRIC_DURATION)
        updateLocationTextField()
        updateDescriptionTextField()
        updatePhoto()
    }

    private fun updateNameTextField(){
        this.nameTextField.setText(this.trail?.name)
    }

    private fun updateLevelTextFieldDropDown(){
        val choice=resources.getStringArray(R.array.array_trail_levels)
        val initialValue=(this.trail?.level?.value?: TrailLevel.MEDIUM.value)-1
        DropdownMenuHelper.populateDropdownMenu(this.levelTextFieldDropDown, choice, initialValue)
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
        val duration=this.trail?.duration
        updateCroller(METRIC_DURATION, duration)
        this.metricsCroller.setOnProgressChangedListener(this)
    }

    private fun initializeDurationText(){
        val durationToDisplay=MetricsHelper.displayDuration(context!!, this.trail?.duration?.toLong())
        this.durationText.text=durationToDisplay
        this.durationText.setOnClickListener {
            val duration=this.trail?.duration
            updateCurrentMetricToSet(METRIC_DURATION)
            updateCroller(METRIC_DURATION, duration)
        }
    }

    private fun initializeAscentText(){
        val ascentToDisplay=MetricsHelper.displayAscent(
            context!!, this.trail?.ascent, true, true)
        this.ascentText.text=ascentToDisplay
        this.ascentText.setOnClickListener {
            val ascent=this.trail?.ascent
            updateCurrentMetricToSet(METRIC_ASCENT)
            updateCroller(METRIC_ASCENT, ascent)
        }
    }

    private fun initializeDescentText(){
        val descentToDisplay=MetricsHelper.displayDescent(
            context!!, this.trail?.descent, true, true)
        this.descentText.text=descentToDisplay
        this.descentText.setOnClickListener {
            val descent=this.trail?.descent
            updateCurrentMetricToSet(METRIC_DESCENT)
            updateCroller(METRIC_DESCENT, descent)
        }
    }

    private fun initializeDistanceText(){
        val distanceToDisplay=MetricsHelper.displayDistance(
            context!!, this.trail?.distance, true, true)
        this.distanceText.text=distanceToDisplay
        this.distanceText.setOnClickListener {
            val distance=this.trail?.distance
            updateCurrentMetricToSet(METRIC_DISTANCE)
            updateCroller(METRIC_DISTANCE, distance)
        }
    }

    private fun initializeMaxElevationText(){
        val maxElevationToDisplay=MetricsHelper.displayMaxElevation(
            context!!, this.trail?.maxElevation, true, true)
        this.maxElevationText.text=maxElevationToDisplay
        this.maxElevationText.setOnClickListener {
            val maxElevation=this.trail?.maxElevation
            updateCurrentMetricToSet(METRIC_MAX_ELEVATION)
            updateCroller(METRIC_MAX_ELEVATION, maxElevation)
        }
    }

    private fun initializeMinElevationText(){
        val minElevationToDisplay=MetricsHelper.displayMinElevation(
            context!!, this.trail?.minElevation, true, true)
        this.minElevationText.text=minElevationToDisplay
        this.minElevationText.setOnClickListener {
            val minElevation=this.trail?.minElevation
            updateCurrentMetricToSet(METRIC_MIN_ELEVATION)
            updateCroller(METRIC_MIN_ELEVATION, minElevation)
        }
    }

    private fun updateLocationTextField(){
        this.locationTextField.setText(this.trail?.location?.fullAddress)
        this.locationTextField.setOnClickListener {
            (activity as TrailInfoEditActivity).searchLocation()
        }
    }

    private fun updateDescriptionTextField(){
        this.descriptionTextField.setText(this.trail?.description)
    }

    private fun updateCurrentMetricToSet(metricToSet: Int){
        this.currentMetricToSet=metricToSet
        for(i in this.metricsTexts.indices){
            if(i==this.currentMetricToSet){
                val color = ContextCompat.getColor(context!!, R.color.colorSecondaryDark)
                this.metricsTexts[i].setTextColor(color)
            }
            else{
                val color = ContextCompat.getColor(context!!, R.color.colorBlack)
                this.metricsTexts[i].setTextColor(color)
            }
        }
    }

    private fun updatePhoto(){
        Glide.with(context!!)
            .load(this.trail?.mainPhotoUrl)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_trail_black)
            .into(this.photoImageView)
        updatePhotoVisibility()
    }

    private fun updatePhotoVisibility(){

        /*If no photo is available, shows a text to notify the user*/

        if(this.trail?.mainPhotoUrl.isNullOrEmpty()){
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
        updateCurrentMetric(progress)
    }

    private fun updateCroller(valueToSet:Int, currentValue:Int?){
        this.currentMetricToSet=valueToSet
        when(this.currentMetricToSet){
            METRIC_DURATION -> this.metricsCroller.max= VALUE_MAX_DURATION
            METRIC_DISTANCE -> this.metricsCroller.max= VALUE_MAX_DISTANCE
            else -> this.metricsCroller.max= VALUE_MAX_ALTITUDE
        }
        this.metricsCroller.progress=currentValue?:0
    }

    private fun updateCurrentMetric(value:Int?){
        when(this.currentMetricToSet){
            METRIC_DURATION -> updateDuration(value?:this.trail?.duration)
            METRIC_ASCENT -> updateAscent(value?:this.trail?.ascent)
            METRIC_DESCENT -> updateDescent(value?:this.trail?.descent)
            METRIC_DISTANCE -> updateDistance(value?:this.trail?.distance?:0)
            METRIC_MAX_ELEVATION -> updateMaxElevation(value?:this.trail?.maxElevation)
            METRIC_MIN_ELEVATION -> updateMinElevation(value?:this.trail?.minElevation)
        }
    }

    private fun updateDuration(duration:Int?){
        this.trail?.duration=duration
        val durationToDisplay=MetricsHelper.displayDuration(context!!, duration?.toLong())
        this.durationText.text=durationToDisplay
        this.metricsCroller.label = durationToDisplay
    }

    private fun updateAscent(ascent:Int?) {
        this.trail?.ascent = ascent
        val ascentToDisplay = MetricsHelper.displayAscent(
            context!!, ascent, true, true)
        val ascentToDisplayInCroller = MetricsHelper.displayAscent(
            context!!, ascent, false, false)
        this.ascentText.text = ascentToDisplay
        this.metricsCroller.label = ascentToDisplayInCroller
    }

    private fun updateDescent(descent:Int?) {
        this.trail?.descent = descent
        val descentToDisplay = MetricsHelper.displayDescent(
            context!!, descent, true, true)
        val descentToDisplayInCroller = MetricsHelper.displayAscent(
            context!!, descent, false, false)
        this.descentText.text = descentToDisplay
        this.metricsCroller.label = descentToDisplayInCroller
    }

    private fun updateDistance(distance:Int) {
        this.trail?.distance = distance
        val distanceToDisplay = MetricsHelper.displayDistance(
            context!!, distance, true, true)
        val distanceToDisplayInCroller = MetricsHelper.displayAscent(
            context!!, distance, false, false)
        this.distanceText.text = distanceToDisplay
        this.metricsCroller.label = distanceToDisplayInCroller
    }

    private fun updateMaxElevation(elevation:Int?) {
        this.trail?.maxElevation = elevation
        val maxElevationToDisplay = MetricsHelper.displayMaxElevation(
            context!!, elevation, true, true)
        val maxElevationToDisplayInCroller = MetricsHelper.displayMaxElevation(
            context!!, elevation, false, false)
        this.maxElevationText.text = maxElevationToDisplay
        this.metricsCroller.label = maxElevationToDisplayInCroller
    }

    private fun updateMinElevation(elevation:Int?) {
        this.trail?.minElevation = elevation
        val minElevationToDisplay = MetricsHelper.displayMinElevation(
            context!!, elevation, true, true)
        val minElevationToDisplayInCroller = MetricsHelper.displayMinElevation(
            context!!, elevation, false, false)
        this.minElevationText.text = minElevationToDisplay
        this.metricsCroller.label = minElevationToDisplayInCroller
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
        this.trail?.mainPhotoUrl=filePath
        updatePhoto()
    }

    private fun deletePhoto(){
        if(!this.trail?.mainPhotoUrl.isNullOrEmpty()) {
            (activity as TrailInfoEditActivity)
                .updateImagePathToDeleteFromDatabase(this.trail?.mainPhotoUrl!!)
            this.trail.mainPhotoUrl = null
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
                        Log.d(TAG,"Permission '${KEY_BUNDLE_PERMISSION_WRITE}' granted")
                        startAddPhoto()
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        //TODO handle
                        Log.d(TAG, "Permission '${KEY_BUNDLE_PERMISSION_WRITE}' denied")
                    }
                }
            }
            KEY_REQUEST_PERMISSION_WRITE_AND_CAMERA ->if(grantResults.isNotEmpty()){
                when(grantResults[0]){
                    PackageManager.PERMISSION_GRANTED -> {
                        Log.d(TAG, "Permission '${KEY_BUNDLE_PERMISSION_WRITE}' granted")
                        Log.d(TAG, "Permission '${KEY_BUNDLE_PERMISSION_CAMERA}' granted")
                        startTakePhoto()
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        //TODO handle
                        Log.d(TAG, "Permission '${KEY_BUNDLE_PERMISSION_WRITE}' denied")
                        Log.d(TAG, "Permission '${KEY_BUNDLE_PERMISSION_CAMERA}' denied")
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
