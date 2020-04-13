package com.sildian.apps.togetrail.trail.info

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.trail.map.BaseTrailMapFragment
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import kotlinx.android.synthetic.main.fragment_trail_poi_info.view.*

/*************************************************************************************************
 * Shows information about a point of interest
 * This fragment should be used as a nested fragment within a BottomSheet
 * @param trailPointOfInterest : the related point of interest
 * @param trailPointOfInterestPosition : the position of the trailPointOfInterest in the trailTrack
 * @param isEditable : true if the info can be edited
 ************************************************************************************************/

class TrailPOIInfoFragment (
    private val trailPointOfInterest: TrailPointOfInterest?=null,
    private val trailPointOfInterestPosition:Int?=null,
    private val isEditable:Boolean=false
)
    : BaseInfoFragment() {

    /**********************************UI component**********************************************/

    private val photoImageView by lazy {layout.fragment_trail_poi_info_image_view_photo}
    private val nameText by lazy {layout.fragment_trail_poi_info_text_name}
    private val editButton by lazy {layout.fragment_trail_poi_info_button_edit}
    private val elevationText by lazy {layout.fragment_trail_poi_info_text_elevation}
    private val descriptionText by lazy {layout.fragment_trail_poi_info_text_description}

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_poi_info

    override fun getTopViewId(): Int = R.id.fragment_trail_poi_info_image_view_photo

    override fun getBottomViewId(): Int = R.id.fragment_trail_poi_info_layout_info

    override fun initializeUI() {
        initializeNameText()
        initializeEditButton()
        initializeElevationText()
        initializeDescriptionText()
        updatePhoto()
    }

    private fun initializeNameText(){
        this.nameText.text=this.trailPointOfInterest?.name
    }

    private fun initializeEditButton(){
        if(isEditable){
            this.editButton.visibility= View.VISIBLE
            this.editButton.setOnClickListener {
                (parentFragment as BaseTrailMapFragment)
                    .editTrailPoiInfo(this.trailPointOfInterestPosition!!)
            }
        }else{
            this.editButton.visibility=View.GONE
        }
    }

    private fun initializeElevationText(){
        val elevationToDisplay=MetricsHelper.displayElevation(
            context!!, this.trailPointOfInterest?.elevation, true, false)
        this.elevationText.text=elevationToDisplay
    }

    private fun initializeDescriptionText(){
        val description=this.trailPointOfInterest?.description
        if(!description.isNullOrEmpty()){
            this.descriptionText.text=description
        }else{
            this.descriptionText.setText(R.string.message_no_description_available)
        }
    }

    private fun updatePhoto(){
        Glide.with(context!!)
            .load(this.trailPointOfInterest?.photoUrl)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_trail_black)
            .into(this.photoImageView)
    }
}
