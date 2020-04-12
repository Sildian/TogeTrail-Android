package com.sildian.apps.togetrail.trail.info

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.trail.map.BaseTrailMapFragment
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import kotlinx.android.synthetic.main.fragment_trail_info.view.*

/*************************************************************************************************
 * Shows information about a trail
 * This fragment should be used as a nested fragment within a BottomSheet
 * @param trail : the related trail
 ************************************************************************************************/

class TrailInfoFragment(val trail: Trail?=null) : Fragment() {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG="TrailInfoFragment"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val nameText by lazy {layout.fragment_trail_info_text_name}
    private val editButton by lazy {layout.fragment_trail_info_button_edit}
    private val levelImage by lazy {layout.fragment_trail_info_image_level}
    private val levelText by lazy {layout.fragment_trail_info_text_level}
    private val durationText by lazy {layout.fragment_trail_info_text_duration}
    private val photoText by lazy {layout.fragment_trail_info_text_photo}
    private val photosRecyclerView by lazy {layout.fragment_trail_info_recycler_view_photos}
    private lateinit var photoAdapter:PhotoAdapter
    private val ascentText by lazy {layout.fragment_trail_info_text_ascent}
    private val descentText by lazy {layout.fragment_trail_info_text_descent}
    private val distanceText by lazy {layout.fragment_trail_info_text_distance}
    private val maxElevationText by lazy {layout.fragment_trail_info_text_max_elevation}
    private val minElevationText by lazy {layout.fragment_trail_info_text_min_elevation}
    private val locationText by lazy {layout.fragment_trail_info_text_location}
    private val descriptionText by lazy {layout.fragment_trail_info_text_description}

    /**************************************Data**************************************************/

    private val photosUrls:ArrayList<String> = arrayListOf()        //The list of photos urls of the trail

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_trail_info, container, false)
        initializeAllUIComponents()
        return this.layout
    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeNameText()
        initializeEditButton()
        initializeLevelImage()
        initializeLevelText()
        initializeDuration()
        initializePhotosRecyclerView()
        initializeAscentText()
        initializeDescentText()
        initializeDistanceText()
        initializeMaxElevationText()
        initializeMinElevationText()
        initializeLocationText()
        initializeDescriptionText()
        updatePhotos()
    }

    private fun initializeNameText(){
        this.nameText.text=this.trail?.name
    }

    private fun initializeEditButton(){
        this.editButton.setOnClickListener {
            (parentFragment as BaseTrailMapFragment).editTrailInfo()
        }
    }

    private fun initializeLevelImage(){
        when(this.trail?.level){
            TrailLevel.UNKNOWN -> this.levelImage.setImageResource(R.drawable.ic_level_medium)
            TrailLevel.EASY-> this.levelImage.setImageResource(R.drawable.ic_level_easy)
            TrailLevel.MEDIUM-> this.levelImage.setImageResource(R.drawable.ic_level_medium)
            TrailLevel.HARD-> this.levelImage.setImageResource(R.drawable.ic_level_hard)
        }
    }

    private fun initializeLevelText(){
        when(this.trail?.level){
            TrailLevel.UNKNOWN -> this.levelText.setText(R.string.label_trail_level_unknown)
            TrailLevel.EASY-> this.levelText.setText(R.string.label_trail_level_easy)
            TrailLevel.MEDIUM-> this.levelText.setText(R.string.label_trail_level_medium)
            TrailLevel.HARD-> this.levelText.setText(R.string.label_trail_level_hard)
        }
    }

    private fun initializeDuration(){
        val durationToDisplay=MetricsHelper.displayDuration(context!!, this.trail?.duration?.toLong())
        this.durationText.text=durationToDisplay
    }

    private fun initializePhotosRecyclerView(){
        this.photoAdapter= PhotoAdapter(this.photosUrls)
        this.photosRecyclerView.adapter=this.photoAdapter
    }

    private fun initializeAscentText(){
        val ascentToDisplay=MetricsHelper.displayAscent(
            context!!, this.trail?.ascent, true, true)
        this.ascentText.text=ascentToDisplay
    }

    private fun initializeDescentText(){
        val descentToDisplay=MetricsHelper.displayDescent(
            context!!, this.trail?.descent, true, true)
        this.descentText.text=descentToDisplay
    }

    private fun initializeDistanceText(){
        val distanceToDisplay=MetricsHelper.displayDistance(
            context!!, this.trail?.distance, true, true)
        this.distanceText.text=distanceToDisplay
    }

    private fun initializeMaxElevationText(){
        val maxElevationToDisplay=MetricsHelper.displayMaxElevation(
            context!!, this.trail?.maxElevation, true, true)
        this.maxElevationText.text=maxElevationToDisplay
    }

    private fun initializeMinElevationText(){
        val minElevationToDisplay=MetricsHelper.displayMinElevation(
            context!!, this.trail?.minElevation, true, true)
        this.minElevationText.text=minElevationToDisplay
    }

    private fun initializeLocationText(){
        val location=this.trail?.location?.fullAddress
        if(!location.isNullOrEmpty()){
            this.locationText.text=location
        }else{
            this.locationText.setText(R.string.message_no_location_available)
        }
    }

    private fun initializeDescriptionText(){
        val description=this.trail?.description
        if(!description.isNullOrEmpty()){
            this.descriptionText.text=description
        }else{
            this.descriptionText.setText(R.string.message_no_description_available)
        }
    }

    private fun updatePhotos(){
        this.photosUrls.clear()
        this.trail?.getAllPhotosUrls()?.let { this.photosUrls.addAll(it) }
        this.photoAdapter.notifyDataSetChanged()
        updatePhotosVisibility()
    }

    private fun updatePhotosVisibility(){
        if(this.photosUrls.isEmpty()){
            this.photoText.visibility=View.VISIBLE
            this.photosRecyclerView.visibility=View.INVISIBLE
        }
        else{
            this.photoText.visibility=View.INVISIBLE
            this.photosRecyclerView.visibility=View.VISIBLE
        }
    }
}
