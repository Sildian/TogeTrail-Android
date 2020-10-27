package com.sildian.apps.togetrail.trail.infoEdit

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.circularsliderlibrary.CircularSlider
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.ValueFormatters
import com.sildian.apps.togetrail.databinding.FragmentTrailPoiInfoEditBinding
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import kotlinx.android.synthetic.main.fragment_trail_poi_info_edit.view.*

/*************************************************************************************************
 * Allows to edit information about a trailPointOfInterest
 * @param trailViewModel : the trail data
 ************************************************************************************************/

class TrailPOIInfoEditFragment(
    private val trailViewModel: TrailViewModel?=null,
    private val trailPointOfInterestPosition:Int?=null
) :
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

    /**************************************Life cycle********************************************/

    override fun onDestroy() {
        this.trailViewModel?.clearImagePaths()
        super.onDestroy()
    }

    /*********************************Data monitoring********************************************/

    override fun loadData() {
        initializeData()
        observeTrail()
        observeTrailPOI()
        observeSaveRequestSuccess()
        observeRequestFailure()
    }

    private fun initializeData() {
        this.binding.lifecycleOwner = this
        (this.binding as FragmentTrailPoiInfoEditBinding).trailPOIInfoEditFragment=this
        (this.binding as FragmentTrailPoiInfoEditBinding).trailViewModel=this.trailViewModel
    }

    private fun observeTrail() {
        this.trailViewModel?.trail?.observe(this) { trail ->
            if (trail != null) {
                this.trailPointOfInterestPosition?.let { position ->
                    this.trailViewModel.watchPointOfInterest(position)
                }
            }
        }
    }

    private fun observeTrailPOI() {
        this.trailViewModel?.trailPointOfInterest?.observe(this) { trailPOI ->
            if (trailPOI != null) {
                refreshUI()
            }
        }
    }

    private fun observeSaveRequestSuccess() {
        this.trailViewModel?.saveRequestSuccess?.observe(this) { success ->
            if (success) {
                onSaveSuccess()
            }
        }
    }

    private fun observeRequestFailure() {
        this.trailViewModel?.requestFailure?.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    override fun saveData() {
        if(checkDataIsValid()) {
            if (this.trailViewModel?.trailPointOfInterest?.value != null) {
                this.trailViewModel.trailPointOfInterest.value?.name = this.nameTextField.text.toString()
                this.trailViewModel.trailPointOfInterest.value?.description = this.descriptionTextField.text.toString()
                this.baseActivity?.showProgressDialog()
                this.trailViewModel.saveTrailInDatabase(true)
            }
        }
    }

    override fun checkDataIsValid(): Boolean {
        if(checkTextFieldsAreNotEmpty()){
            return true
        }else{
            SnackbarHelper
                .createSimpleSnackbar(this.messageView, this.messageAnchorView, R.string.message_text_fields_empty)
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
        initializeResetMetricsButton()
        refreshUI()
    }

    override fun refreshUI() {
        updateMetricsSlider()
        updateElevationText()
        updatePhoto()
    }

    private fun initializeMetricsSlider(){
        this.metricsSlider.addOnValueChangedListener(this)
    }

    private fun updateMetricsSlider() {
        val elevation=this.trailViewModel?.trailPointOfInterest?.value?.elevation
        updateMetricsSlider(elevation)
    }

    private fun updateElevationText(){
        val elevation=this.trailViewModel?.trailPointOfInterest?.value?.elevation
        updateMetricsSlider(elevation)
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
            .load(this.trailViewModel?.trailPointOfInterest?.value?.photoUrl)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_trail_black)
            .into(this.photoImageView)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onDeletePhotoButtonClick(view:View){
        deletePhoto()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddPhotoButtonClick(view:View){
        expandAddPhotoBottomSheet()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSelectPhotoButtonClick(view:View){
        requestWritePermission()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTakePhotoButtonClick(view:View){
        requestWriteAndCameraPermission()
    }

    /***********************************Metrics monitoring***************************************/

    override fun onValueChanged(view: CircularSlider, value: Int) {
        updateElevation(value)
    }

    private fun updateMetricsSlider(currentValue:Int?){
        this.metricsSlider.valueFormatter= ValueFormatters.AltitudeValueFormatter()
        this.metricsSlider.setMaxValue(VALUE_MAX_ALTITUDE)
        this.metricsSlider.setCurrentValue(currentValue?:0)
    }

    private fun updateElevation(elevation:Int?){
        this.trailViewModel?.trailPointOfInterest?.value?.elevation=elevation
        val elevationToDisplay=MetricsHelper.displayElevation(
            context!!, elevation, true, true)
        this.elevationText.text=elevationToDisplay
    }

    /*******************************Photos monitoring********************************************/

    override fun addPhoto(filePath:String){
        this.trailViewModel?.updateImagePathToUpload(true, filePath)
        this.trailViewModel?.trailPointOfInterest?.value?.photoUrl=filePath
        this.trailViewModel?.notifyDataChanged()
        updatePhoto()
    }

    override fun deletePhoto(){
        if(!this.trailViewModel?.trailPointOfInterest?.value?.photoUrl.isNullOrEmpty()) {
            this.trailViewModel?.updateImagePathToDelete(this.trailViewModel.trailPointOfInterest.value?.photoUrl!!)
            this.trailViewModel?.trailPointOfInterest?.value?.photoUrl = null
            this.trailViewModel?.notifyDataChanged()
            updatePhoto()
        }
    }
}
