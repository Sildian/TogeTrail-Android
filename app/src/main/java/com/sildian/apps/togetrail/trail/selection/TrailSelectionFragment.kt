package com.sildian.apps.togetrail.trail.selection

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.android.synthetic.main.fragment_trail_selection.view.*

/*************************************************************************************************
 * This fragment displays a query to let the user select a trail to attach to an event
 * @param trailsQuery : the query to display
 * @param selectedTrails : the list of selected trails
 ************************************************************************************************/

class TrailSelectionFragment(private val trailsQuery:Query, private val selectedTrails:List<Trail>) :
    BaseFragment(),
    TrailSelectionViewHolder.OnTrailSelectListener,
    TrailSelectionViewHolder.OnTrailClickListener
{

    /**********************************UI component**********************************************/

    private val trailsRecyclerView by lazy {layout.fragment_trail_selection_recycler_view_trails}
    private lateinit var trailsAdapter:TrailSelectionAdapter

    /*************************************UI monitoring******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_selection

    override fun useDataBinding(): Boolean = false

    override fun initializeUI() {
        initializeTrailsRecyclerView()
    }

    private fun initializeTrailsRecyclerView(){
        this.trailsAdapter= TrailSelectionAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
            Trail::class.java,
            this.trailsQuery,
            activity as AppCompatActivity
            ),
            this.selectedTrails,
            this,
            this
        )
        this.trailsRecyclerView.adapter=this.trailsAdapter
    }

    /***********************************Trails monitoring****************************************/

    override fun onTrailSelected(trail: Trail) {
        (activity as TrailSelectionActivity).addSelectedTrail(trail)
    }

    override fun onTrailUnSelected(trail: Trail) {
        (activity as TrailSelectionActivity).removeSelectedTrail(trail)
    }

    override fun onTrailClick(trail: Trail) {
        (activity as TrailSelectionActivity).seeTrail(trail)
    }
}
