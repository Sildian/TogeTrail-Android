package com.sildian.apps.togetrail.trail.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.cloudHelpers.RecyclerViewFirebaseHelper
import com.sildian.apps.togetrail.common.utils.cloudHelpers.UserFirebaseHelper
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
    Fragment(),
    TrailHorizontalViewHolder.OnTrailClickListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG_FRAGMENT="TAG_FRAGMENT"
        private const val TAG_UI="TAG_UI"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val lastAddedTrailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_last_added_trails }
    private lateinit var lastAddedTrailsAdapter:TrailHorizontalAdapter
    private val myTrailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_my_trails}
    private lateinit var myTrailsAdapter:TrailHorizontalAdapter
    private val nearbyTrailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_nearby_trails}
    private lateinit var nearbyTrailsAdapter:TrailHorizontalAdapter

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_trails_list, container, false)
        initializeLastAddedTrailsRecyclerView()
        initializeMyTrailsRecyclerView()
        initializeNearbyTrailsRecyclerView()
        return this.layout
    }

    /***********************************UI monitoring********************************************/

    private fun initializeLastAddedTrailsRecyclerView(){
        this.lastAddedTrailsAdapter= TrailHorizontalAdapter(
            RecyclerViewFirebaseHelper.generateOptionsForAdapter(
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
                RecyclerViewFirebaseHelper.generateOptionsForAdapter(
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
                RecyclerViewFirebaseHelper.generateOptionsForAdapter(
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
        Log.d(TAG_UI, "Click on trail '${trail.id}")
        (activity as MainActivity).seeTrail(trail)
    }
}
