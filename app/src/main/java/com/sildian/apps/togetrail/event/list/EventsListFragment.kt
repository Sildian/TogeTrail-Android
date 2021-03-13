package com.sildian.apps.togetrail.event.list

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.event.others.EventHorizontalAdapter
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel
import com.sildian.apps.togetrail.main.MainActivity
import kotlinx.android.synthetic.main.fragment_events_list.view.*

/*************************************************************************************************
 * Shows the lists of events on the screen, using different queries to populate it
 * @param hikerViewModel : the current user
 ************************************************************************************************/

class EventsListFragment (private val hikerViewModel: HikerViewModel?=null) :
    BaseFragment(),
    EventHorizontalAdapter.OnEventClickListener
{

    /**********************************UI component**********************************************/

    private val currentResearchEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_current_research}
    private lateinit var currentResearchEventsAdapter:EventHorizontalAdapter
    private val iAttendEventsText by lazy {layout.fragment_events_list_text_i_attend}
    private val iAttendEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_i_attend}
    private lateinit var iAttendEventsAdapter:EventHorizontalAdapter
    private val nextEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_next_events}
    private lateinit var nextEventsAdapter:EventHorizontalAdapter
    private val myEventsText by lazy {layout.fragment_events_list_text_my_events}
    private val myEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_my_events}
    private lateinit var myEventsAdapter:EventHorizontalAdapter
    private val nearbyHomeEventsText by lazy {layout.fragment_events_list_text_nearby_home}
    private val nearbyHomeEventsRecyclerView by lazy {layout.fragment_events_list_recycler_view_nearby_home}
    private lateinit var nearbyHomeEventsAdapter:EventHorizontalAdapter

    /***********************************Data monitoring******************************************/

    override fun updateData(data: Any?) {
        if(data is Query){
            updateCurrentResearchEventsRecyclerView()
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_events_list

    override fun useDataBinding(): Boolean = false

    override fun initializeUI() {
        updateCurrentResearchEventsRecyclerView()
        initializeIAttendEventsRecyclerView()
        initializeNextEventsRecyclerView()
        initializeMyEventsRecyclerView()
        initializeNearbyHomeEventsRecyclerView()
    }

    private fun updateCurrentResearchEventsRecyclerView(){
        this.currentResearchEventsAdapter= EventHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Event::class.java,
                (activity as MainActivity).eventsQuery,
                activity as AppCompatActivity
            ), this
        )
        this.currentResearchEventsRecyclerView.adapter=this.currentResearchEventsAdapter
    }

    private fun initializeIAttendEventsRecyclerView() {
        if (this.hikerViewModel?.hiker?.value != null && this.hikerViewModel.hiker.value?.nbEventsAttended!! > 0) {
            this.iAttendEventsAdapter = EventHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    HikerFirebaseQueries.getAttendedEvents(this.hikerViewModel.hiker.value?.id!!),
                    activity as AppCompatActivity
                ), this
            )
            this.iAttendEventsRecyclerView.adapter = this.iAttendEventsAdapter
        }else{
            this.iAttendEventsText.visibility= View.GONE
            this.iAttendEventsRecyclerView.visibility=View.GONE
        }
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
        if (this.hikerViewModel?.hiker?.value != null && this.hikerViewModel.hiker.value?.nbEventsCreated!! > 0) {
            this.myEventsAdapter = EventHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    EventFirebaseQueries.getMyEvents(this.hikerViewModel.hiker.value?.id!!),
                    activity as AppCompatActivity
                ), this
            )
            this.myEventsRecyclerView.adapter = this.myEventsAdapter
        }else{
            this.myEventsText.visibility=View.GONE
            this.myEventsRecyclerView.visibility=View.GONE
        }
    }

    private fun initializeNearbyHomeEventsRecyclerView(){
        if(this.hikerViewModel?.hiker?.value?.liveLocation?.country!=null) {
            this.nearbyHomeEventsAdapter = EventHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Event::class.java,
                    EventFirebaseQueries.getEventsNearbyLocation(this.hikerViewModel.hiker.value?.liveLocation!!)!!,
                    activity as AppCompatActivity
                ), this
            )
            this.nearbyHomeEventsRecyclerView.adapter = this.nearbyHomeEventsAdapter
        }else{
            this.nearbyHomeEventsText.visibility=View.GONE
            this.nearbyHomeEventsRecyclerView.visibility=View.GONE
        }
    }

    /***********************************Events monitoring****************************************/

    override fun onEventClick(event: Event) {
        (activity as MainActivity).seeEvent(event)
    }
}
