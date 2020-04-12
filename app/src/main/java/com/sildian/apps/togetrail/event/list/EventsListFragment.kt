package com.sildian.apps.togetrail.event.list

import androidx.appcompat.app.AppCompatActivity
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
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

    /**********************************UI component**********************************************/

    private val nextEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_next_events}
    private lateinit var nextEventsAdapter:EventHorizontalAdapter
    private val myEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_my_events}
    private lateinit var myEventsAdapter:EventHorizontalAdapter
    private val nearbyEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_nearby_events}
    private lateinit var nearbyEventsAdapter:EventHorizontalAdapter

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_events_list

    override fun initializeUI() {
        initializeNextEventsRecyclerView()
        initializeMyEventsRecyclerView()
        initializeNearbyEventsRecyclerView()
    }

    override fun refreshUI() {
        //Nothing
    }

    private fun initializeNextEventsRecyclerView(){
        this.nextEventsAdapter= EventHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
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
                DatabaseFirebaseHelper.generateOptionsForAdapter(
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
                DatabaseFirebaseHelper.generateOptionsForAdapter(
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
        (activity as MainActivity).seeEvent(event)
    }
}
