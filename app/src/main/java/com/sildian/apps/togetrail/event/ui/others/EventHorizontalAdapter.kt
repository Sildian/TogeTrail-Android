package com.sildian.apps.togetrail.event.ui.others

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewEventHorizontalBinding
import com.sildian.apps.togetrail.event.data.models.Event

/*************************************************************************************************
 * Displays a list of events within an horizontal RecyclerView
 ************************************************************************************************/

class EventHorizontalAdapter(
    options:FirestoreRecyclerOptions<Event>,
    private val listener: OnEventClickListener? = null
)
    : FirestoreRecyclerAdapter<Event, EventHorizontalAdapter.EventHorizontalViewHolder> (options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHorizontalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewEventHorizontalBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_recycler_view_event_horizontal, parent, false)
        return EventHorizontalViewHolder(binding, this.listener)
    }

    override fun onBindViewHolder(holder: EventHorizontalViewHolder, position: Int, event: Event) {
        holder.bind(event)
    }

    /***********************************Callbacks************************************************/

    interface OnEventClickListener {
        fun onEventClick(event: Event)
    }

    /***********************************ViewHolder************************************************/

    class EventHorizontalViewHolder(
        private val binding: ItemRecyclerViewEventHorizontalBinding,
        private val listener: OnEventClickListener? = null
    )
        : RecyclerView.ViewHolder(binding.root)
    {

        /*************************************Data************************************************/

        private lateinit var event: Event

        /*************************************Init************************************************/

        init {
            this.binding.eventHorizontalViewHolder = this
        }

        /*******************************UI monitoring*********************************************/

        fun bind(event: Event) {
            this.event = event
            this.binding.event = this.event
        }

        @Suppress("UNUSED_PARAMETER")
        fun onEventClick(view: View) {
            this.listener?.onEventClick(this.event)
        }
    }
}