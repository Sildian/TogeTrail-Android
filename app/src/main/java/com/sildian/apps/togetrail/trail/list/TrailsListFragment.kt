package com.sildian.apps.togetrail.trail.list

import androidx.appcompat.app.AppCompatActivity
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder
import kotlinx.android.synthetic.main.fragment_trails_list.view.*

/*************************************************************************************************
 * Shows the lists of trails on the screen, using different queries to populate it
 * @param currentHiker : the current hiker
 ************************************************************************************************/

class TrailsListFragment (private val currentHiker: Hiker?=null) :
    BaseDataFlowFragment(),
    TrailHorizontalViewHolder.OnTrailClickListener
{

    /**********************************UI component**********************************************/

    private val lastAddedTrailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_last_added_trails }
    private lateinit var lastAddedTrailsAdapter:TrailHorizontalAdapter
    private val myTrailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_my_trails}
    private lateinit var myTrailsAdapter:TrailHorizontalAdapter
    private val nearbyTrailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_nearby_trails}
    private lateinit var nearbyTrailsAdapter:TrailHorizontalAdapter

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trails_list

    override fun initializeUI() {
        initializeLastAddedTrailsRecyclerView()
        initializeMyTrailsRecyclerView()
        initializeNearbyTrailsRecyclerView()
    }

    override fun refreshUI() {
        //Nothing
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

        //TODO hide if the query return no item

        if(this.currentHiker!=null) {
            this.myTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    TrailFirebaseQueries.getMyTrails(this.currentHiker.id),
                    activity as AppCompatActivity
                ), this
            )
            this.myTrailsRecyclerView.adapter = this.myTrailsAdapter
        }
    }

    private fun initializeNearbyTrailsRecyclerView(){

        //TODO hide if the query return no item

        if(this.currentHiker?.liveLocation?.country!=null) {
            this.nearbyTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    TrailFirebaseQueries.getTrailsNearbyLocation(this.currentHiker.liveLocation)!!,
                    activity as AppCompatActivity
                ), this
            )
            this.nearbyTrailsRecyclerView.adapter = this.nearbyTrailsAdapter
        }
    }

    /***********************************Trails monitoring****************************************/

    override fun onTrailClick(trail: Trail) {
        (activity as MainActivity).seeTrail(trail)
    }
}
