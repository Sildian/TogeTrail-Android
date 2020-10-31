package com.sildian.apps.togetrail.event.others

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.event.model.core.Event
import kotlinx.android.synthetic.main.item_recycler_view_event_horizontal.view.*

/*************************************************************************************************
 * Displays an event's main info within an horizontal RecyclerView
 ************************************************************************************************/

class EventHorizontalViewHolder(
    itemView:View,
    private val listener:OnEventClickListener?=null
)
    :RecyclerView.ViewHolder(itemView)
{

    /***********************************Callbacks************************************************/

    interface OnEventClickListener{
        fun onEventClick(event: Event)
    }

    /**************************************Data**************************************************/

    private lateinit var event:Event

    /**********************************UI components*********************************************/

    private val photoImageView by lazy {itemView.item_recycler_view_event_horizontal_image_view_photo}
    private val nameText by lazy {itemView.item_recycler_view_event_horizontal_text_name}
    private val beginDateText by lazy {itemView.item_recycler_view_event_horizontal_text_begin_date}
    private val nbDaysText by lazy {itemView.item_recycler_view_event_horizontal_text_nb_days}
    private val locationText by lazy {itemView.item_recycler_view_event_horizontal_text_location}

    /**************************************Init**************************************************/

    init{
        itemView.setOnClickListener { this.listener?.onEventClick(this.event) }
    }

    /************************************UI update***********************************************/

    fun updateUI(event:Event){
        this.event=event
        updatePhotoImageView()
        updateNameText()
        updateBeginDateText()
        updateNbDaysText()
        updateLocationText()
    }

    private fun updatePhotoImageView(){
        if(this.event.mainPhotoUrl!=null) {
            Glide.with(this.itemView)
                .load(this.event.mainPhotoUrl)
                .apply(RequestOptions.centerCropTransform())
                .placeholder(R.drawable.ic_trail_black)
                .into(this.photoImageView)
        }else{
            this.photoImageView.setImageResource(R.drawable.ic_trail_black)
        }
    }

    private fun updateNameText(){
        this.nameText.text=this.event.name
    }

    private fun updateBeginDateText(){
        if(this.event.beginDate!=null) {
            this.beginDateText.text = DateUtilities.displayDateShort(this.event.beginDate!!)
        }else{
            this.beginDateText.text=""
        }
    }

    private fun updateNbDaysText(){
        if(this.event.getNbDays()!=null){
            val nbDays=this.event.getNbDays()
            val metric=if(nbDays!!>1){
                itemView.resources.getString(R.string.label_event_days)
            }else{
                itemView.resources.getString(R.string.label_event_day)
            }
            val nbDaysToDisplay="$nbDays $metric"
            this.nbDaysText.text=nbDaysToDisplay
        }else{
            this.nbDaysText.text=""
        }
    }

    private fun updateLocationText(){
        this.locationText.text=this.event.meetingPoint.toString()
    }
}