package com.sildian.apps.togetrail.event.ui.list

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.FragmentEventsListBinding
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.event.data.source.EventFirebaseQueries
import com.sildian.apps.togetrail.event.ui.others.EventHorizontalAdapter
import com.sildian.apps.togetrail.hiker.data.source.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Shows the lists of events on the screen, using different queries to populate it
 ************************************************************************************************/

@AndroidEntryPoint
class EventsListFragment:
    BaseFragment<FragmentEventsListBinding>(),
    EventHorizontalAdapter.OnEventClickListener
{

    /**********************************UI component**********************************************/

    private lateinit var currentResearchEventsAdapter: EventHorizontalAdapter
    private lateinit var iAttendEventsAdapter: EventHorizontalAdapter
    private lateinit var nextEventsAdapter: EventHorizontalAdapter
    private lateinit var myEventsAdapter: EventHorizontalAdapter
    private lateinit var nearbyHomeEventsAdapter: EventHorizontalAdapter

    /***********************************Data monitoring******************************************/

    override fun updateData(data: Any?) {
        if (data is Query) {
            updateCurrentResearchEventsRecyclerView()
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_events_list

    override fun initializeUI() {
        updateCurrentResearchEventsRecyclerView()
        initializeIAttendEventsRecyclerView()
        initializeNextEventsRecyclerView()
        initializeMyEventsRecyclerView()
        initializeNearbyHomeEventsRecyclerView()
    }

    private fun updateCurrentResearchEventsRecyclerView() {
        this.currentResearchEventsAdapter= EventHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Event::class.java,
                (activity as MainActivity).eventsQuery,
                activity as AppCompatActivity
            ), this
        )
        this.binding.fragmentEventsListRecyclerViewCurrentResearch.adapter = this.currentResearchEventsAdapter
    }

    private fun initializeIAttendEventsRecyclerView() {
        CurrentHikerInfo.currentHiker?.takeIf { it.nbEventsAttended > 0 }?.let { currentHiker ->
            this.iAttendEventsAdapter = EventHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    HikerFirebaseQueries.getAttendedEvents(currentHiker.id),
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentEventsListRecyclerViewIAttend.adapter = this.iAttendEventsAdapter
        } ?: run {
            this.binding.fragmentEventsListTextIAttend.visibility = View.GONE
            this.binding.fragmentEventsListRecyclerViewIAttend.visibility = View.GONE
        }
    }

    private fun initializeNextEventsRecyclerView() {
        this.nextEventsAdapter = EventHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Event::class.java,
                EventFirebaseQueries.getNextEvents(),
                activity as AppCompatActivity
            ), this
        )
        this.binding.fragmentEventsListRecyclerViewNextEvents.adapter = this.nextEventsAdapter
    }

    private fun initializeMyEventsRecyclerView() {
        CurrentHikerInfo.currentHiker?.takeIf { it.nbEventsCreated > 0 }?.let { currentHiker ->
            this.myEventsAdapter = EventHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    EventFirebaseQueries.getMyEvents(currentHiker.id),
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentEventsListRecyclerViewMyEvents.adapter = this.myEventsAdapter
        } ?: run {
            this.binding.fragmentEventsListTextMyEvents.visibility = View.GONE
            this.binding.fragmentEventsListRecyclerViewMyEvents.visibility = View.GONE
        }
    }

    private fun initializeNearbyHomeEventsRecyclerView() {
        CurrentHikerInfo.currentHiker?.takeIf { it.liveLocation.country != null }?.let { currentHiker ->
            this.nearbyHomeEventsAdapter = EventHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    EventFirebaseQueries.getEventsNearbyLocation(currentHiker.liveLocation)!!,
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentEventsListRecyclerViewNearbyHome.adapter = this.nearbyHomeEventsAdapter
        } ?: run {
            this.binding.fragmentEventsListTextNearbyHome.visibility = View.GONE
            this.binding.fragmentEventsListRecyclerViewNearbyHome.visibility = View.GONE
        }
    }

    /***********************************Events monitoring****************************************/

    override fun onEventClick(event: Event) {
        (activity as MainActivity).seeEvent(event)
    }
}
