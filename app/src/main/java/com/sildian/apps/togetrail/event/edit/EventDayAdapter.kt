package com.sildian.apps.togetrail.event.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Allows the user to attach a trail to a specific day for an event
 * @param trailsIds : a HashMap with the number of each day as keys and the trails ids as entries
 ************************************************************************************************/

class EventDayAdapter(
    private val trailsIds:HashMap<String, String?>,
    private val listener: EventDayViewHolder.OnEventDayTrailChanged?=null
)
    :RecyclerView.Adapter<EventDayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventDayViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_recycler_view_event_day, parent, false)
        return EventDayViewHolder(view, this.listener)
    }

    override fun getItemCount(): Int {
        return this.trailsIds.size
    }

    override fun onBindViewHolder(holder: EventDayViewHolder, position: Int) {
        holder.updateUI(position+1, this.trailsIds[(position+1).toString()])
    }
}