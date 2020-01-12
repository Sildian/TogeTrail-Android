package com.sildian.apps.togetrail.trail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.trail.model.Trail
import com.sildian.apps.togetrail.trail.model.TrailLevel
import com.sildian.apps.togetrail.trail.model.TrailType
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
        const val TAG_FRAGMENT="TAG_FRAGMENT"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val nameText by lazy {layout.fragment_trail_info_text_name}
    private val levelText by lazy {layout.fragment_trail_info_text_level}
    private val typeText by lazy {layout.fragment_trail_info_text_type}
    private val durationText by lazy {layout.fragment_trail_info_text_duration}
    private val ascentText by lazy {layout.fragment_trail_info_text_ascent}
    private val descentText by lazy {layout.fragment_trail_info_text_descent}
    private val distanceText by lazy {layout.fragment_trail_info_text_distance}
    private val maxElevationText by lazy {layout.fragment_trail_info_text_max_elevation}
    private val minElevationText by lazy {layout.fragment_trail_info_text_min_elevation}
    private val descriptionText by lazy {layout.fragment_trail_info_text_description}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_trail_info, container, false)
        initializeAllUIComponents()
        return this.layout
    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeNameText()
        initializeLevelText()
        initializeTypeText()
        initializeDuration()
        initializeAscentText()
        initializeDescentText()
        initializeDistanceText()
        initializeMaxElevationText()
        initializeMinElevationText()
        initializeDescriptionText()
    }

    private fun initializeNameText(){
        this.nameText.text=this.trail?.name
    }

    private fun initializeLevelText(){
        when(this.trail?.level){
            TrailLevel.EASY->{
                this.levelText.compoundDrawablesRelative[0]=
                    resources.getDrawable(R.drawable.ic_level_easy)
                this.levelText.setText(R.string.label_trail_level_easy)
            }
            TrailLevel.MEDIUM->{
                this.levelText.compoundDrawablesRelative[0]=
                    resources.getDrawable(R.drawable.ic_level_medium)
                this.levelText.setText(R.string.label_trail_level_medium)
            }
            TrailLevel.HARD->{
                this.levelText.compoundDrawablesRelative[0]=
                    resources.getDrawable(R.drawable.ic_level_hard)
                this.levelText.setText(R.string.label_trail_level_hard)
            }
        }
    }

    private fun initializeTypeText(){
        when(this.trail?.type){
            TrailType.HIKING->{
                this.typeText.compoundDrawablesRelative[0]=
                    resources.getDrawable(R.drawable.ic_hiking_white)
                this.typeText.setText(R.string.label_trail_type_hiking)
            }
            TrailType.BIKING->{
                this.typeText.compoundDrawablesRelative[0]=
                    resources.getDrawable(R.drawable.ic_biking_white)
                this.typeText.setText(R.string.label_trail_type_biking)
            }
            TrailType.OTHER->{
                this.typeText.compoundDrawablesRelative[0]=
                    resources.getDrawable(R.drawable.ic_trail_white)
                this.typeText.setText(R.string.label_trail_type_other)
            }
        }
    }

    private fun initializeDuration(){
        val duration=this.trail?.trailTrack?.getDuration()
        this.durationText.text=
            if(duration!=null)DateUtilities.displayDuration(duration.toLong()) else "?"
    }

    private fun initializeAscentText(){
        val ascent=this.trail?.trailTrack?.getAscent()
        val metric=resources.getString(R.string.label_trail_ascent_short)
        this.ascentText.text=
            if(ascent!=null)"$ascent m\n$metric" else "?"
    }

    private fun initializeDescentText(){
        val descent=this.trail?.trailTrack?.getDescent()
        val metric=resources.getString(R.string.label_trail_descent_short)
        this.descentText.text=
            if(descent!=null)"$descent m\n$metric" else "?"
    }

    private fun initializeDistanceText(){
        val distance=this.trail?.trailTrack?.getDistance()
        val metric=resources.getString(R.string.label_trail_distance_short)
        this.distanceText.text=
            if(distance!=null)"$distance m\n$metric" else "?"
    }

    private fun initializeMaxElevationText(){
        val maxElevation=this.trail?.trailTrack?.getMaxElevation()
        val metric=resources.getString(R.string.label_trail_max_elevation_short)
        this.maxElevationText.text=
            if(maxElevation!=null)"$maxElevation m\n$metric" else "?"
    }

    private fun initializeMinElevationText(){
        val minElevation=this.trail?.trailTrack?.getMinElevation()
        val metric=resources.getString(R.string.label_trail_min_elevation_short)
        this.minElevationText.text=
            if(minElevation!=null)"$minElevation m\n$metric" else "?"
    }

    private fun initializeDescriptionText(){
        val description=this.trail?.description
        this.descriptionText.text=
            if(!description.isNullOrEmpty())description else "No description is available"
    }
}
