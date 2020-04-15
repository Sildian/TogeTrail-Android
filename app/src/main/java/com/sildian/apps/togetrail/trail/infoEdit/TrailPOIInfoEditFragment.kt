package com.sildian.apps.togetrail.trail.infoEdit

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.common.views.circularSlider.CircularSlider
import com.sildian.apps.togetrail.common.views.circularSlider.ValueFormaters
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import kotlinx.android.synthetic.main.fragment_trail_poi_info_edit.view.*

/*************************************************************************************************
 * Allows to edit information about a trailPointOfInterest
 * @param trailPointOfInterest : the related trailPointOfInterest
 ************************************************************************************************/

class TrailPOIInfoEditFragment(private val trailPointOfInterest: TrailPointOfInterest?=null) :
    BaseImagePickerFragment(),
    CircularSlider.OnValueChangedListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Values max**/
        private const val VALUE_MAX_ALTITUDE=4000       //Max value for an altitude (in meters)
    }

    /**********************************UI component**********************************************/

    private val nameTextFieldLayout by lazy {layout.fragment_trail_poi_info_edit_text_field_layout_name}
    private val nameTextField by lazy {layout.fragment_trail_poi_info_edit_text_field_name}
    private val photoText by lazy {layout.fragment_trail_poi_info_edit_text_photo}
    private val photoImageView by lazy {layout.fragment_trail_poi_info_edit_image_view_photo}
    private val deletePhotoButton by lazy {layout.fragment_trail_poi_info_edit_button_delete_photo}
    private val addPhotoButton by lazy {layout.fragment_trail_poi_info_edit_button_add_photo}
    private val metricsSlider by lazy {layout.fragment_trail_poi_info_edit_slider_metrics}
    private val elevationText by lazy {layout.fragment_trail_poi_info_edit_text_elevation}
    private val resetMetricsButton by lazy {layout.fragment_trail_poi_info_edit_button_metrics_reset}
    private val descriptionTextField by lazy {layout.fragment_trail_poi_info_edit_text_field_description}
    private val selectPhotoButton by lazy {layout.fragment_trail_poi_info_edit_button_select_photo}
    private val takePhotoButton by lazy {layout.fragment_trail_poi_info_edit_button_take_photo}
    private val messageView by lazy {layout.fragment_trail_poi_info_edit_view_message}
    private val messageAnchorView by lazy {layout.fragment_trail_poi_info_edit_bottom_sheet_add_photo}

    /*********************************Data monitoring********************************************/

    override fun saveData() {
        if(checkDataIsValid()) {
            if (this.trailPointOfInterest != null) {
                this.trailPointOfInterest.name = this.nameTextField.text.toString()
                this.trailPointOfInterest.description = this.descriptionTextField.text.toString()
                (activity as TrailInfoEditActivity).saveTrailPoi(this.trailPointOfInterest)
            }
        }
    }

    override fun checkDataIsValid(): Boolean {
        if(checkTextFieldsAreNotEmpty()){
            return true
        }else{
            Snackbar.make(this.messageView, R.string.message_text_fields_empty, Snackbar.LENGTH_LONG)
                .setAnchorView(this.messageAnchorView)
                .show()
        }
        return false
    }

    private fun checkTextFieldsAreNotEmpty():Boolean{
        return TextFieldHelper.checkTextFieldIsNotEmpty(this.nameTextField, this.nameTextFieldLayout)
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_poi_info_edit

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_trail_poi_info_edit_bottom_sheet_add_photo

    override fun initializeUI() {
        initializeDeletePhotoButton()
        initializeAddPhotoButton()
        initializeMetricsSlider()
        initializeElevationText()
        initializeResetMetricsButton()
        initializeSelectPhotoButton()
        initializeTakePhotoButton()
        refreshUI()
    }

    override fun refreshUI() {
        updateNameTextField()
        updateDescriptionTextField()
        updatePhoto()
    }

    private fun updateNameTextField(){
        this.nameTextField.setText(this.trailPointOfInterest?.name)
    }

    private fun initializeDeletePhotoButton(){
        this.deletePhotoButton.setOnClickListener {
            deletePhoto()
        }
    }

    private fun initializeAddPhotoButton(){
        this.addPhotoButton.setOnClickListener {
            expandAddPhotoBottomSheet()
        }
    }

    private fun initializeMetricsSlider(){
        val elevation=this.trailPointOfInterest?.elevation
        updateMetricsSlider(elevation)
        this.metricsSlider.addOnValueChangedListener(this)
    }

    private fun initializeElevationText(){
        val elevation=this.trailPointOfInterest?.elevation
        updateElevation(elevation)
    }

    private fun initializeResetMetricsButton(){
        this.resetMetricsButton.setOnClickListener {
            updateMetricsSlider(null)
            updateElevation(null)
        }
    }

    private fun updateDescriptionTextField(){
        this.descriptionTextField.setText(this.trailPointOfInterest?.description)
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

    private fun updatePhoto(){
        Glide.with(context!!)
            .load(this.trailPointOfInterest?.photoUrl)
            .apply(RequestOptions.centerCropTransform())
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

    /***********************************Metrics monitoring***************************************/

    override fun onValueChanged(view: CircularSlider, value: Int) {
        updateElevation(value)
    }

    private fun updateMetricsSlider(currentValue:Int?){
        this.metricsSlider.valueFormater=ValueFormaters.AltitudeValueFormater()
        this.metricsSlider.setMaxValue(VALUE_MAX_ALTITUDE)
        this.metricsSlider.setCurrentValue(currentValue?:0)
    }

    private fun updateElevation(elevation:Int?){
        this.trailPointOfInterest?.elevation=elevation
        val elevationToDisplay=MetricsHelper.displayElevation(
            context!!, elevation, true, true)
        this.elevationText.text=elevationToDisplay
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String){
        (activity as TrailInfoEditActivity)
            .updateImagePathToUploadIntoDatabase(filePath)
        this.trailPointOfInterest?.photoUrl=filePath
        updatePhoto()
    }

    override fun deletePhoto(){
        if(!this.trailPointOfInterest?.photoUrl.isNullOrEmpty()) {
            (activity as TrailInfoEditActivity)
                .updateImagePathToDeleteFromDatabase(this.trailPointOfInterest?.photoUrl!!)
            this.trailPointOfInterest.photoUrl = null
            updatePhoto()
        }
    }
}
