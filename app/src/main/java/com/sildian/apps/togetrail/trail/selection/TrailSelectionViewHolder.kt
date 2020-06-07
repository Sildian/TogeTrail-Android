package com.sildian.apps.togetrail.trail.selection

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import kotlinx.android.synthetic.main.item_recycler_view_trail_selection.view.*

/*************************************************************************************************
 * Displays a trail's main info with a purpose to select it
 ************************************************************************************************/

class TrailSelectionViewHolder (
    itemView:View,
    private val onTrailSelectListener: OnTrailSelectListener,
    private val onTrailClickListener: OnTrailClickListener?=null
)
    :RecyclerView.ViewHolder(itemView)
{

    /***********************************Callbacks************************************************/

    interface OnTrailSelectListener{
        fun onTrailSelected(trail:Trail)
        fun onTrailUnSelected(trail:Trail)
    }

    interface OnTrailClickListener{
        fun onTrailClick(trail: Trail)
    }

    /**************************************Data**************************************************/

    private lateinit var trail: Trail                               //The related trail

    /**********************************UI components*********************************************/

    private val checkbox by lazy {itemView.item_recycler_view_trail_selection_checkbox}
    private val infoLayout by lazy {itemView.item_recycler_view_trail_selection_layout_info}
    private val photoImageView by lazy {itemView.item_recycler_view_trail_selection_image_view_photo}
    private val nameText by lazy {itemView.item_recycler_view_trail_selection_text_name}
    private val levelText by lazy {itemView.item_recycler_view_trail_selection_text_level}
    private val durationText by lazy {itemView.item_recycler_view_trail_selection_text_duration}
    private val ascentText by lazy {itemView.item_recycler_view_trail_selection_text_ascent}
    private val locationText by lazy {itemView.item_recycler_view_trail_selection_text_location}

    /**************************************Init**************************************************/

    init{
        setOnTrailClickListener()
        setOnTrailSelectListener()
    }

    private fun setOnTrailClickListener(){
        this.infoLayout.setOnClickListener { this.onTrailClickListener?.onTrailClick(this.trail) }
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun setOnTrailSelectListener(){
        this.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked){
                true -> this.onTrailSelectListener.onTrailSelected(this.trail)
                false -> this.onTrailSelectListener.onTrailUnSelected(this.trail)
            }
        }
    }

    /************************************UI update***********************************************/

    fun updateUI(trail:Trail, isSelected:Boolean){
        this.trail=trail
        updateCheckbox(isSelected)
        updatePhotoImageView()
        updateNameText()
        updateLevelText()
        updateDurationText()
        updateAscentText()
        updateLocationText()
    }

    private fun updateCheckbox(isSelected: Boolean){
        this.checkbox.isChecked=isSelected
    }

    private fun updatePhotoImageView(){

        /*Fetches photos within the trail*/

        val photoUrl=this.trail.getFirstPhotoUrl()

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
            TrailLevel.UNKNOWN -> this.levelText.setText(R.string.label_trail_level_unknown)
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
        this.locationText.text=this.trail.location.fullAddress
    }
}