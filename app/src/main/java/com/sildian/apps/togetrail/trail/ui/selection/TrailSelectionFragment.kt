package com.sildian.apps.togetrail.trail.ui.selection

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.FragmentTrailSelectionBinding
import com.sildian.apps.togetrail.trail.data.core.Trail

/*************************************************************************************************
 * This fragment displays a query to let the user select a trail to attach to an event
 * @param trailsQuery : the query to display
 * @param selectedTrails : the list of selected trails
 ************************************************************************************************/

class TrailSelectionFragment(private val trailsQuery:Query? = null, private val selectedTrails:List<Trail>? = null) :
    BaseFragment<FragmentTrailSelectionBinding>(),
    TrailSelectionAdapter.OnTrailSelectListener,
    TrailSelectionAdapter.OnTrailClickListener
{

    /**********************************UI component**********************************************/

    private lateinit var trailsAdapter:TrailSelectionAdapter

    /*************************************UI monitoring******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_selection

    override fun initializeUI() {
        initializeTrailsRecyclerView()
    }

    private fun initializeTrailsRecyclerView() {
        if (this.trailsQuery != null && this.selectedTrails != null) {
            this.trailsAdapter = TrailSelectionAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Trail::class.java,
                    this.trailsQuery,
                    activity as AppCompatActivity
                ),
                this.selectedTrails,
                this,
                this
            )
            this.binding.fragmentTrailSelectionRecyclerViewTrails.adapter = this.trailsAdapter
        }
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
