package com.sildian.apps.togetrail.trail.list

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.FragmentTrailsListBinding
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerFirebaseQueries
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.dataRepository.TrailFirebaseQueries
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Shows the lists of trails on the screen, using different queries to populate it
 ************************************************************************************************/

@AndroidEntryPoint
class TrailsListFragment:
    BaseFragment<FragmentTrailsListBinding>(),
    TrailHorizontalViewHolder.OnTrailClickListener
{

    /**********************************UI component**********************************************/

    private lateinit var currentResearchTrailsAdapter: TrailHorizontalAdapter
    private lateinit var lastAddedTrailsAdapter: TrailHorizontalAdapter
    private lateinit var myTrailsAdapter: TrailHorizontalAdapter
    private lateinit var nearbyHomeTrailsAdapter: TrailHorizontalAdapter
    private lateinit var likedTrailsAdapter: TrailHorizontalAdapter
    private lateinit var markedTrailsAdapter: TrailHorizontalAdapter

    /***********************************Data monitoring******************************************/

    override fun updateData(data: Any?) {
        if (data is Query) {
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
        initializeLikedTrailsRecyclerView()
        initializeMarkedTrailsRecyclerView()
    }

    private fun updateCurrentResearchTrailsRecyclerView() {
        this.currentResearchTrailsAdapter= TrailHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java,
                (activity as MainActivity).trailsQuery,
                activity as AppCompatActivity
            ), this)
        this.binding.fragmentTrailsListRecyclerViewCurrentResearch.adapter = this.currentResearchTrailsAdapter
    }

    private fun initializeLastAddedTrailsRecyclerView() {
        this.lastAddedTrailsAdapter= TrailHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java,
                TrailFirebaseQueries.getLastTrails(),
                activity as AppCompatActivity
            ), this)
        this.binding.fragmentTrailsListRecyclerViewLastAddedTrails.adapter = this.lastAddedTrailsAdapter
    }

    private fun initializeMyTrailsRecyclerView() {
        CurrentHikerInfo.currentHiker?.takeIf { it.nbTrailsCreated > 0 }?.let { currentHiker ->
            this.myTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    TrailFirebaseQueries.getMyTrails(currentHiker.id),
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentTrailsListRecyclerViewMyTrails.adapter = this.myTrailsAdapter
        } ?: run {
            this.binding.fragmentTrailsListTextMyTrails.visibility = View.GONE
            this.binding.fragmentTrailsListRecyclerViewMyTrails.visibility = View.GONE
        }
    }

    private fun initializeNearbyHomeTrailsRecyclerView() {
        CurrentHikerInfo.currentHiker?.takeIf { it.liveLocation.country != null }?.let { currentHiker ->
            this.nearbyHomeTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    TrailFirebaseQueries.getTrailsNearbyLocation(currentHiker.liveLocation)!!,
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentTrailsListRecyclerViewNearbyHome.adapter = this.nearbyHomeTrailsAdapter
        } ?: run {
            this.binding.fragmentTrailsListTextNearbyHome.visibility = View.GONE
            this.binding.fragmentTrailsListRecyclerViewNearbyHome.visibility = View.GONE
        }
    }

    private fun initializeLikedTrailsRecyclerView() {
        CurrentHikerInfo.currentHiker?.let { currentHiker ->
            this.likedTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    HikerFirebaseQueries.getLikedTrails(currentHiker.id),
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentTrailsListRecyclerViewLiked.adapter = this.likedTrailsAdapter
        } ?: run {
            this.binding.fragmentTrailsListTextLiked.visibility = View.GONE
            this.binding.fragmentTrailsListRecyclerViewLiked.visibility = View.GONE
        }
    }

    private fun initializeMarkedTrailsRecyclerView() {
        CurrentHikerInfo.currentHiker?.let { currentHiker ->
            this.markedTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    HikerFirebaseQueries.getMarkedTrails(currentHiker.id),
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentTrailsListRecyclerViewMarked.adapter = this.markedTrailsAdapter
        } ?: run {
            this.binding.fragmentTrailsListTextMarked.visibility = View.GONE
            this.binding.fragmentTrailsListRecyclerViewMarked.visibility = View.GONE
        }
    }

    /***********************************Trails monitoring****************************************/

    override fun onTrailClick(trail: Trail) {
        (activity as MainActivity).seeTrail(trail)
    }
}
