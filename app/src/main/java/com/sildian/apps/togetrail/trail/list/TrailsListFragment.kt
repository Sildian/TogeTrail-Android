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
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.map.TrailActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder
import kotlinx.android.synthetic.main.fragment_trails_list.view.*

/*************************************************************************************************
 * Shows the lists of trails on the screen, using different queries to populate it
 ************************************************************************************************/

class TrailsListFragment :
    Fragment(),
    TrailHorizontalViewHolder.OnTrailClickListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG_FRAGMENT="TAG_FRAGMENT"
        private const val TAG_UI="TAG_UI"
    }

    /**************************************Data**************************************************/

    private val trails:List<Trail> = listOf()               //The list of trails to display

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val trailsRecyclerView by lazy {layout.fragment_trails_list_recycler_view_trails }
    private lateinit var trailsAdapter:TrailHorizontalAdapter

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_trails_list, container, false)
        initializeTrailsRecyclerView()
        return this.layout
    }

    /***********************************UI monitoring********************************************/

    private fun initializeTrailsRecyclerView(){
        this.trailsAdapter= TrailHorizontalAdapter(
            RecyclerViewFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java,
                TrailFirebaseQueries.getTrails(),
                activity as AppCompatActivity
            ), this)
        this.trailsRecyclerView.adapter=this.trailsAdapter
    }

    override fun onTrailClick(trail: Trail) {
        Log.d(TAG_UI, "Click on trail '${trail.id}")
        (activity as MainActivity).startTrailActivity(TrailActivity.ACTION_TRAIL_SEE, trail)
    }
}
