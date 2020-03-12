package com.sildian.apps.togetrail.event.others

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.event.model.core.Event

/*************************************************************************************************
 * Displays a list of events within an horizontal RecyclerView
 ************************************************************************************************/

class EventHorizontalAdapter(
    options:FirestoreRecyclerOptions<Event>,
    private val listener:EventHorizontalViewHolder.OnEventClickListener?=null
)
    :FirestoreRecyclerAdapter<Event, EventHorizontalViewHolder> (options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHorizontalViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_recycler_view_event_horizontal, parent, false)
        return EventHorizontalViewHolder(view, this.listener)
    }

    override fun onBindViewHolder(holder: EventHorizontalViewHolder, position: Int, event: Event) {
        holder.updateUI(event)
    }
}