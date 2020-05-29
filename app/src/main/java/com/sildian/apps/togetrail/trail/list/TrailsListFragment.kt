package com.sildian.apps.togetrail.trail.list

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder
import kotlinx.android.synthetic.main.fragment_trails_list.view.*

/*************************************************************************************************
 * Shows the lists of trails on the screen, using different queries to populate it
 * @param hikerViewModel : the current user
 ************************************************************************************************/

class TrailsListFragment (private val hikerViewModel: HikerViewModel?=null) :
    BaseDataFlowFragment(),
    TrailHorizontalViewHolder.OnTrailClickListener
{

    /**********************************UI component**********************************************/

    private val currentResearchTrailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_current_research}
    private lateinit var currentResearchTrailsAdapter:TrailHorizontalAdapter
    private val lastAddedTrailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_last_added_trails }
    private lateinit var lastAddedTrailsAdapter:TrailHorizontalAdapter
    private val myTrailsText by lazy {layout.fragment_trails_list_text_my_trails}
    private val myTrailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_my_trails}
    private lateinit var myTrailsAdapter:TrailHorizontalAdapter
    private val nearbyHomeTrailsText by lazy {layout.fragment_trails_list_text_nearby_home}
    private val nearbyHomeTrailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_nearby_home}
    private lateinit var nearbyHomeTrailsAdapter:TrailHorizontalAdapter

    /***********************************Data monitoring******************************************/

    override fun updateData(data: Any?) {
        if(data is Query){
            updateCurrentResearchTrailsRecyclerView()
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trails_list

    override fun initializeUI() {
        updateCurrentResearchTrailsRecyclerView()
        initializeLastAddedTrailsRecyclerView()
        initializeMyTrailsRecyclerView()
        initializeNearbyHomeTrailsRecyclerView()
    }

    private fun updateCurrentResearchTrailsRecyclerView(){
        this.currentResearchTrailsAdapter= TrailHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java,
                (activity as MainActivity).trailsQuery,
                activity as AppCompatActivity
            ), this)
        this.currentResearchTrailsRecyclerView.adapter=this.currentResearchTrailsAdapter
    }

    private fun initializeLastAddedTrailsRecyclerView(){
        this.lastAddedTrailsAdapter= TrailHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java,
                TrailFirebaseQueries.getLastTrails(),
                activity as AppCompatActivity
            ), this)
        this.lastAddedTrailsRecyclerView.adapter=this.lastAddedTrailsAdapter
    }

    private fun initializeMyTrailsRecyclerView(){
        if(this.hikerViewModel?.hiker!=null&&this.hikerViewModel.hiker?.nbTrailsCreated!!>0) {
            this.myTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    TrailFirebaseQueries.getMyTrails(this.hikerViewModel.hiker?.id!!),
                    activity as AppCompatActivity
                ), this
            )
            this.myTrailsRecyclerView.adapter = this.myTrailsAdapter
        }
        else{
            this.myTrailsText.visibility= View.GONE
            this.myTrailsRecyclerView.visibility=View.GONE
        }
    }

    private fun initializeNearbyHomeTrailsRecyclerView(){
        if(this.hikerViewModel?.hiker?.liveLocation?.country!=null) {
            this.nearbyHomeTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    TrailFirebaseQueries.getTrailsNearbyLocation(this.hikerViewModel.hiker?.liveLocation!!)!!,
                    activity as AppCompatActivity
                ), this
            )
            this.nearbyHomeTrailsRecyclerView.adapter = this.nearbyHomeTrailsAdapter
        }
        else{
            this.nearbyHomeTrailsText.visibility= View.GONE
            this.nearbyHomeTrailsRecyclerView.visibility=View.GONE
        }
    }

    /***********************************Trails monitoring****************************************/

    override fun onTrailClick(trail: Trail) {
        (activity as MainActivity).seeTrail(trail)
    }
}
