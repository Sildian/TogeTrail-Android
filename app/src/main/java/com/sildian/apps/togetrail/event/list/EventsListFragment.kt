package com.sildian.apps.togetrail.event.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.cloudHelpers.RecyclerViewFirebaseHelper
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.event.others.EventHorizontalAdapter
import com.sildian.apps.togetrail.event.others.EventHorizontalViewHolder
import com.sildian.apps.togetrail.main.MainActivity
import kotlinx.android.synthetic.main.fragment_events_list.view.*

/*************************************************************************************************
 * Shows the lists of events on the screen, using different queries to populate it
 ************************************************************************************************/

class EventsListFragment :
    Fragment(),
    EventHorizontalViewHolder.OnEventClickListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG_FRAGMENT="TAG_FRAGMENT"
        private const val TAG_UI="TAG_UI"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val eventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_events}
    private lateinit var eventsAdapter:EventHorizontalAdapter

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout= inflater.inflate(R.layout.fragment_events_list, container, false)
        initializeEventsRecyclerView()
        return this.layout
    }

    /***********************************UI monitoring********************************************/

    private fun initializeEventsRecyclerView(){
        this.eventsAdapter= EventHorizontalAdapter(
            RecyclerViewFirebaseHelper.generateOptionsForAdapter(
                Event::class.java,
                EventFirebaseQueries.getEvents(),
                activity as AppCompatActivity
            ), this
        )
        this.eventsRecyclerView.adapter=this.eventsAdapter
    }

    override fun onEventClick(event: Event) {
        Log.d(TAG_UI, "Click on event '${event.id}")
        (activity as MainActivity).seeEvent(event)
    }
}
