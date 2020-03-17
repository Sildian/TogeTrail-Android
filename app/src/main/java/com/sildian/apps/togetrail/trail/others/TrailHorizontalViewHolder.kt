package com.sildian.apps.togetrail.trail.others

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import kotlinx.android.synthetic.main.item_recycler_view_trail_horizontal.view.*

/*************************************************************************************************
 * Displays a trail's main info within an horizontal RecyclerView
 ************************************************************************************************/

class TrailHorizontalViewHolder (
    itemView: View,
    private val onTrailClickListener: OnTrailClickListener?=null,
    private val isEditable:Boolean=false,
    private val onTrailRemovedListener: OnTrailRemovedListener?=null
) :
    RecyclerView.ViewHolder(itemView)
{

    /***********************************Callbacks************************************************/

    interface OnTrailClickListener{
        fun onTrailClick(trail:Trail)
    }

    interface OnTrailRemovedListener{
        fun onTrailRemoved(trail:Trail)
    }

    /**************************************Data**************************************************/

    private lateinit var trail: Trail                               //The related trail

    /**********************************UI components*********************************************/

    private val photoImageView by lazy {itemView.item_recycler_view_trail_horizontal_image_view_photo}
    private val nameText by lazy {itemView.item_recycler_view_trail_horizontal_text_name}
    private val levelText by lazy {itemView.item_recycler_view_trail_horizontal_text_level}
    private val durationText by lazy {itemView.item_recycler_view_trail_horizontal_text_duration}
    private val ascentText by lazy {itemView.item_recycler_view_trail_horizontal_text_ascent}
    private val locationText by lazy {itemView.item_recycler_view_trail_horizontal_text_location}
    private val removeButton by lazy {itemView.item_recycler_view_trail_horizontal_button_remove}

    /**************************************Init**************************************************/

    init{
        this.itemView.setOnClickListener { this.onTrailClickListener?.onTrailClick(this.trail) }
        this.removeButton.setOnClickListener { this.onTrailRemovedListener?.onTrailRemoved(this.trail) }
    }

    /************************************UI update***********************************************/

    fun updateUI(trail:Trail){
        this.trail=trail
        updatePhotoImageView()
        updateRemoveButton()
        updateNameText()
        updateLevelText()
        updateDurationText()
        updateAscentText()
        updateLocationText()
    }

    private fun updatePhotoImageView(){

        /*Fetches photos within the trail*/

        val photoUrl=
            this.trail.trailTrack.trailPointsOfInterest.firstOrNull { trailPointOfInterest ->
                trailPointOfInterest.photoUrl!=null
        }?.photoUrl

        /*Shows a photo only if at least one photo exists*/

        if(photoUrl!=null) {
            Glide.with(this.itemView)
                .load(photoUrl)
                .apply(RequestOptions.centerCropTransform())
                .placeholder(R.drawable.ic_trail_black)
                .into(this.photoImageView)
        }else{
            this.photoImageView.setImageResource(R.drawable.ic_trail_black)
        }
    }

    private fun updateNameText(){
        this.nameText.text=this.trail.name
    }

    private fun updateLevelText(){
        when(this.trail.level){
            TrailLevel.EASY -> this.levelText.setText(R.string.label_trail_level_easy)
            TrailLevel.MEDIUM -> this.levelText.setText(R.string.label_trail_level_medium)
            TrailLevel.HARD -> this.levelText.setText(R.string.label_trail_level_hard)
        }
    }

    private fun updateDurationText(){
        val durationToDisplay=
            MetricsHelper.displayDuration(itemView.context, this.trail.duration?.toLong())
        this.durationText.text=durationToDisplay
    }

    private fun updateAscentText(){
        val ascentToDisplay=
            MetricsHelper.displayAscent(itemView.context, this.trail.ascent, true, false)
        this.ascentText.text=ascentToDisplay
    }

    private fun updateLocationText(){
        this.locationText.text=this.trail.location.getFullLocation()
    }

    private fun updateRemoveButton(){
        if(this.isEditable){
            this.removeButton.visibility=View.VISIBLE
        }else{
            this.removeButton.visibility=View.GONE
        }
    }
}