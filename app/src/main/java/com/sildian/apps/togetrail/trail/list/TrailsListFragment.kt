package com.sildian.apps.togetrail.trail.list

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.FragmentTrailsListBinding
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder

/*************************************************************************************************
 * Shows the lists of trails on the screen, using different queries to populate it
 * @param hikerViewModel : the current user
 ************************************************************************************************/

class TrailsListFragment (private val hikerViewModel: HikerViewModel?=null) :
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
        initializeLikedTrailsRecyclerView()
        initializeMarkedTrailsRecyclerView()
    }

    private fun updateCurrentResearchTrailsRecyclerView(){
        this.currentResearchTrailsAdapter= TrailHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java,
                (activity as MainActivity).trailsQuery,
                activity as AppCompatActivity
            ), this)
        this.binding.fragmentTrailsListRecyclerViewCurrentResearch.adapter = this.currentResearchTrailsAdapter
    }

    private fun initializeLastAddedTrailsRecyclerView(){
        this.lastAddedTrailsAdapter= TrailHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java,
                TrailFirebaseQueries.getLastTrails(),
                activity as AppCompatActivity
            ), this)
        this.binding.fragmentTrailsListRecyclerViewLastAddedTrails.adapter = this.lastAddedTrailsAdapter
    }

    private fun initializeMyTrailsRecyclerView(){
        if (this.hikerViewModel?.data?.value != null && this.hikerViewModel.data.value?.nbTrailsCreated!! > 0) {
            this.myTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    TrailFirebaseQueries.getMyTrails(this.hikerViewModel.data.value?.id!!),
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentTrailsListRecyclerViewMyTrails.adapter = this.myTrailsAdapter
        }
        else {
            this.binding.fragmentTrailsListTextMyTrails.visibility = View.GONE
            this.binding.fragmentTrailsListRecyclerViewMyTrails.visibility = View.GONE
        }
    }

    private fun initializeNearbyHomeTrailsRecyclerView(){
        if (this.hikerViewModel?.data?.value?.liveLocation?.country!=null) {
            this.nearbyHomeTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    TrailFirebaseQueries.getTrailsNearbyLocation(this.hikerViewModel.data.value?.liveLocation!!)!!,
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentTrailsListRecyclerViewNearbyHome.adapter = this.nearbyHomeTrailsAdapter
        }
        else {
            this.binding.fragmentTrailsListTextNearbyHome.visibility = View.GONE
            this.binding.fragmentTrailsListRecyclerViewNearbyHome.visibility = View.GONE
        }
    }

    private fun initializeLikedTrailsRecyclerView(){
        if (CurrentHikerInfo.currentHiker != null) {
            this.likedTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    HikerFirebaseQueries.getLikedTrails(CurrentHikerInfo.currentHiker?.id!!),
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentTrailsListRecyclerViewLiked.adapter = this.likedTrailsAdapter
        }
        else {
            this.binding.fragmentTrailsListTextLiked.visibility = View.GONE
            this.binding.fragmentTrailsListRecyclerViewLiked.visibility = View.GONE
        }
    }

    private fun initializeMarkedTrailsRecyclerView(){
        if (CurrentHikerInfo.currentHiker != null) {
            this.markedTrailsAdapter = TrailHorizontalAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    HikerFirebaseQueries.getMarkedTrails(CurrentHikerInfo.currentHiker?.id!!),
                    activity as AppCompatActivity
                ), this
            )
            this.binding.fragmentTrailsListRecyclerViewMarked.adapter = this.markedTrailsAdapter
        }
        else {
            this.binding.fragmentTrailsListTextMarked.visibility = View.GONE
            this.binding.fragmentTrailsListRecyclerViewMarked.visibility = View.GONE
        }
    }

    /***********************************Trails monitoring****************************************/

    override fun onTrailClick(trail: Trail) {
        (activity as MainActivity).seeTrail(trail)
    }
}
