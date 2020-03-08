package com.sildian.apps.togetrail.event.edit

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import kotlinx.android.synthetic.main.item_recycler_view_event_day.view.*

/*************************************************************************************************
 * Allows the user to attach a trail to a specific day for an event
 ************************************************************************************************/

class EventDayViewHolder (
    itemView: View,
    private val listener:OnEventDayTrailChanged?=null
)
    : RecyclerView.ViewHolder(itemView){

    companion object{

        /**Logs**/
        private const val TAG_STORAGE="TAG_STORAGE"
    }

    /**************************************Callbacks*********************************************/

    interface OnEventDayTrailChanged{
        fun onTrailAdd(day:Int, trailId:String?)
        fun onTrailRemove(day: Int, trailId: String?)
    }

    /***************************************Data*************************************************/

    private var day:Int=0
    private var trailId:String?=null

    /**********************************UI components*********************************************/

    private val dayText by lazy {itemView.item_recycler_view_event_day_text_day}
    private val addTrailButton by lazy {itemView.item_recycler_view_event_day_button_trail_add}
    private val trailLayout by lazy {itemView.item_recycler_view_event_day_layout_trail}
    private val trailNameText by lazy {itemView.item_recycler_view_event_day_text_trail_name}
    private val removeTrailButton by lazy {itemView.item_recycler_view_event_day_button_trail_remove}

    /**************************************Init**************************************************/

    init{
        this.addTrailButton.setOnClickListener { this.listener?.onTrailAdd(this.day, this.trailId) }
        this.removeTrailButton.setOnClickListener { this.listener?.onTrailRemove(this.day, this.trailId) }
    }

    /************************************UI update***********************************************/

    fun updateUI(day:Int, trailId:String?){
        this.day=day
        this.trailId=trailId
        updateDay()
        updateAddTrailButton()
        updateTrailLayout()
        updateTrailName()
        getTrailInfoFromDatabase()
    }

    private fun updateDay(){
        val dayToDisplay=this.itemView.resources.getString(R.string.label_event_day) + " ${this.day}"
        this.dayText.text=dayToDisplay
    }

    private fun updateAddTrailButton(){
        if(this.trailId.isNullOrEmpty()){
            this.addTrailButton.visibility=View.VISIBLE
        }
        else{
            this.addTrailButton.visibility=View.GONE
        }
    }

    private fun updateTrailLayout(){
        if(this.trailId.isNullOrEmpty()){
            this.trailLayout.visibility=View.GONE
        }
        else{
            this.trailLayout.visibility=View.VISIBLE
        }
    }

    private fun updateTrailName(){
        this.trailNameText.text=""
    }

    /*********************************Data monitoring********************************************/

    private fun getTrailInfoFromDatabase(){
        if(this.trailId!=null) {
            TrailFirebaseQueries.getTrail(this.trailId!!)
                .addOnSuccessListener { documentSnapshot ->
                    val trail=documentSnapshot.toObject(Trail::class.java)
                    val trailName=trail?.name
                    this.trailNameText.text=trailName
                }
                .addOnFailureListener { e->
                    Log.w(TAG_STORAGE, e.message.toString())
                    //TODO handle
                }
        }
    }
}