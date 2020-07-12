package com.sildian.apps.togetrail.trail.infoEdit

import android.view.View
import androidx.databinding.Observable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.common.views.circularSlider.CircularSlider
import com.sildian.apps.togetrail.common.views.circularSlider.ValueFormaters
import com.sildian.apps.togetrail.databinding.FragmentTrailPoiInfoEditBinding
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import kotlinx.android.synthetic.main.fragment_trail_poi_info_edit.view.*

/*************************************************************************************************
 * Allows to edit information about a trailPointOfInterest
 * @param trailViewModel : the trail data
 ************************************************************************************************/

class TrailPOIInfoEditFragment(private val trailViewModel: TrailViewModel?=null) :
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
    private val photoImageView by lazy {layout.fragment_trail_poi_info_edit_image_view_photo}
    private val metricsSlider by lazy {layout.fragment_trail_poi_info_edit_slider_metrics}
    private val elevationText by lazy {layout.fragment_trail_poi_info_edit_text_elevation}
    private val resetMetricsButton by lazy {layout.fragment_trail_poi_info_edit_button_metrics_reset}
    private val descriptionTextField by lazy {layout.fragment_trail_poi_info_edit_text_field_description}
    private val messageView by lazy {layout.fragment_trail_poi_info_edit_view_message}
    private val messageAnchorView by lazy {layout.fragment_trail_poi_info_edit_bottom_sheet_add_photo}

    /*********************************Data monitoring********************************************/

    override fun loadData() {
        (this.binding as FragmentTrailPoiInfoEditBinding).trailPOIInfoEditFragment=this
        (this.binding as FragmentTrailPoiInfoEditBinding).trailViewModel=this.trailViewModel
        this.trailViewModel?.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                refreshUI()
            }
        })
    }

    override fun saveData() {
        if(checkDataIsValid()) {
            if (this.trailViewModel?.trailPointOfInterest != null) {
                this.trailViewModel?.trailPointOfInterest?.name = this.nameTextField.text.toString()
                this.trailViewModel?.trailPointOfInterest?.description = this.descriptionTextField.text.toString()
                this.baseActivity?.showProgressDialog()
                this.trailViewModel?.saveTrailInDatabase(true, this::handleSaveDataSuccess, this::handleQueryError)
            }
        }
    }

    private fun handleSaveDataSuccess(){
        this.baseActivity?.dismissProgressDialog()
        (activity as TrailInfoEditActivity).finishOk()
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

    override fun useDataBinding(): Boolean = true

    override fun getAddPhotoBottomSheetId(): Int = R.id.fragment_trail_poi_info_edit_bottom_sheet_add_photo

    override fun initializeUI() {
        initializeMetricsSlider()
        initializeElevationText()
        initializeResetMetricsButton()
        refreshUI()
    }

    override fun refreshUI() {
        updatePhoto()
    }

    private fun initializeMetricsSlider(){
        val elevation=this.trailViewModel?.trailPointOfInterest?.elevation
        updateMetricsSlider(elevation)
        this.metricsSlider.addOnValueChangedListener(this)
    }

    private fun initializeElevationText(){
        val elevation=this.trailViewModel?.trailPointOfInterest?.elevation
        updateElevation(elevation)
    }

    private fun initializeResetMetricsButton(){
        this.resetMetricsButton.setOnClickListener {
            updateMetricsSlider(null)
            updateElevation(null)
        }
    }

    private fun updatePhoto(){
        Glide.with(context!!)
            .load(this.trailViewModel?.trailPointOfInterest?.photoUrl)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_trail_black)
            .into(this.photoImageView)
    }

    fun onDeletePhotoButtonClick(view:View){
        deletePhoto()
    }

    fun onAddPhotoButtonClick(view:View){
        expandAddPhotoBottomSheet()
    }

    fun onSelectPhotoButtonClick(view:View){
        requestWritePermission()
    }

    fun onTakePhotoButtonClick(view:View){
        requestWriteAndCameraPermission()
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
        this.trailViewModel?.trailPointOfInterest?.elevation=elevation
        val elevationToDisplay=MetricsHelper.displayElevation(
            context!!, elevation, true, true)
        this.elevationText.text=elevationToDisplay
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String){
        this.trailViewModel?.updateImagePathToUpload(true, filePath)
        this.trailViewModel?.trailPointOfInterest?.photoUrl=filePath
        this.trailViewModel?.notifyDataChanged()
        updatePhoto()
    }

    override fun deletePhoto(){
        if(!this.trailViewModel?.trailPointOfInterest?.photoUrl.isNullOrEmpty()) {
            this.trailViewModel?.updateImagePathToDelete(this.trailViewModel?.trailPointOfInterest?.photoUrl!!)
            this.trailViewModel?.trailPointOfInterest?.photoUrl = null
            this.trailViewModel?.notifyDataChanged()
            updatePhoto()
        }
    }
}
