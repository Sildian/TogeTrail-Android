package com.sildian.apps.togetrail.event.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.RecyclerViewFirebaseHelper
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.event.others.EventHorizontalAdapter
import com.sildian.apps.togetrail.event.others.EventHorizontalViewHolder
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.main.MainActivity
import kotlinx.android.synthetic.main.fragment_events_list.view.*

/*************************************************************************************************
 * Shows the lists of events on the screen, using different queries to populate it
 * @param currentHiker : the current hiker
 ************************************************************************************************/

class EventsListFragment (private val currentHiker:Hiker?=null) :
    BaseDataFlowFragment(),
    EventHorizontalViewHolder.OnEventClickListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG="EventListFragment"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val nextEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_next_events}
    private lateinit var nextEventsAdapter:EventHorizontalAdapter
    private val myEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_my_events}
    private lateinit var myEventsAdapter:EventHorizontalAdapter
    private val nearbyEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_nearby_events}
    private lateinit var nearbyEventsAdapter:EventHorizontalAdapter

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "Fragment '${javaClass.simpleName}' created")
        this.layout= inflater.inflate(R.layout.fragment_events_list, container, false)
        initializeNextEventsRecyclerView()
        initializeMyEventsRecyclerView()
        initializeNearbyEventsRecyclerView()
        return this.layout
    }

    /***********************************UI monitoring********************************************/

    private fun initializeNextEventsRecyclerView(){
        this.nextEventsAdapter= EventHorizontalAdapter(
            RecyclerViewFirebaseHelper.generateOptionsForAdapter(
                Event::class.java,
                EventFirebaseQueries.getNextEvents(),
                activity as AppCompatActivity
            ), this
        )
        this.nextEventsRecyclerView.adapter=this.nextEventsAdapter
    }

    private fun initializeMyEventsRecyclerView() {

        //TODO hide if the query return no item

        if (this.currentHiker!= null) {
            this.myEventsAdapter = EventHorizontalAdapter(
                RecyclerViewFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    EventFirebaseQueries.getMyEvents(this.currentHiker.id),
                    activity as AppCompatActivity
                ), this
            )
            this.myEventsRecyclerView.adapter = this.myEventsAdapter
        }
    }

    private fun initializeNearbyEventsRecyclerView(){

        //TODO hide if the query return no item

        if(this.currentHiker?.liveLocation?.country!=null) {
            this.nearbyEventsAdapter = EventHorizontalAdapter(
                RecyclerViewFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    EventFirebaseQueries.getEventsNearbyLocation(this.currentHiker.liveLocation)!!,
                    activity as AppCompatActivity
                ), this
            )
            this.nearbyEventsRecyclerView.adapter = this.nearbyEventsAdapter
        }
    }

    /***********************************Events monitoring****************************************/

    override fun onEventClick(event: Event) {
        Log.d(TAG, "Clicked on event '${event.id}")
        (activity as MainActivity).seeEvent(event)
    }
}
