package com.sildian.apps.togetrail.event.list

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.FragmentEventsListBinding
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.dataRepository.EventFirebaseQueries
import com.sildian.apps.togetrail.event.others.EventHorizontalAdapter
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.viewModels.HikerViewModel
import com.sildian.apps.togetrail.main.MainActivity

/*************************************************************************************************
 * Shows the lists of events on the screen, using different queries to populate it
 * @param hikerViewModel : the current user
 ************************************************************************************************/

class EventsListFragment (private val hikerViewModel: HikerViewModel?=null) :
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
        if(data is Query){
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
        if (this.hikerViewModel?.data?.value != null && this.hikerViewModel.data.value?.nbEventsAttended!! > 0) {
            this.iAttendEventsAdapter = EventHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    HikerFirebaseQueries.getAttendedEvents(this.hikerViewModel.data.value?.id!!),
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentEventsListRecyclerViewIAttend.adapter = this.iAttendEventsAdapter
        } else {
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
        if (this.hikerViewModel?.data?.value != null && this.hikerViewModel.data.value?.nbEventsCreated!! > 0) {
            this.myEventsAdapter = EventHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    EventFirebaseQueries.getMyEvents(this.hikerViewModel.data.value?.id!!),
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentEventsListRecyclerViewMyEvents.adapter = this.myEventsAdapter
        } else {
            this.binding.fragmentEventsListTextMyEvents.visibility = View.GONE
            this.binding.fragmentEventsListRecyclerViewMyEvents.visibility = View.GONE
        }
    }

    private fun initializeNearbyHomeEventsRecyclerView() {
        if (this.hikerViewModel?.data?.value?.liveLocation?.country!=null) {
            this.nearbyHomeEventsAdapter = EventHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    EventFirebaseQueries.getEventsNearbyLocation(this.hikerViewModel.data.value?.liveLocation!!)!!,
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentEventsListRecyclerViewNearbyHome.adapter = this.nearbyHomeEventsAdapter
        } else {
            this.binding.fragmentEventsListTextNearbyHome.visibility = View.GONE
            this.binding.fragmentEventsListRecyclerViewNearbyHome.visibility = View.GONE
        }
    }

    /***********************************Events monitoring****************************************/

    override fun onEventClick(event: Event) {
        (activity as MainActivity).seeEvent(event)
    }
}
