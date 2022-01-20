package com.sildian.apps.togetrail.trail.info

import android.view.View
import androidx.databinding.ViewDataBinding
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.FragmentTrailPoiInfoBinding
import com.sildian.apps.togetrail.trail.map.BaseTrailMapFragment
import com.sildian.apps.togetrail.trail.model.viewModels.TrailViewModel

/*************************************************************************************************
 * Shows information about a point of interest
 * This fragment should be used as a nested fragment within a BottomSheet
 * @param trailViewModel : the trail data
 * @param trailPointOfInterestPosition : the position of the trailPointOfInterest in the trailTrack
 * @param isEditable : true if the info can be edited
 ************************************************************************************************/

class TrailPOIInfoFragment (
    private val trailViewModel: TrailViewModel? = null,
    private val trailPointOfInterestPosition:Int? = null,
    private val isEditable:Boolean=false
)
    : BaseInfoFragment<FragmentTrailPoiInfoBinding>() {

    /*********************************Data monitoring********************************************/

    override fun loadData() {
        initializeData()
        observeTrailPOI()
        observeRequestFailure()
    }

    private fun initializeData() {
        this.trailPointOfInterestPosition?.let { position ->
            this.trailViewModel?.watchPointOfInterest(position)
        }
        this.binding.trailPOIInfoFragment = this
        this.binding.trailViewModel = this.trailViewModel
        this.binding.isEditable = this.isEditable
    }

    private fun observeTrailPOI() {
        this.trailViewModel?.trailPointOfInterest?.observe(this) { trailPOI ->
            if (trailPOI != null) {
                refreshUI()
            }
        }
    }

    private fun observeRequestFailure() {
        this.trailViewModel?.error?.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_poi_info

    override fun getTopViewId(): Int = R.id.fragment_trail_poi_info_image_view_photo

    override fun getBottomViewId(): Int = R.id.fragment_trail_poi_info_layout_info

    @Suppress("UNUSED_PARAMETER")
    fun onSeeButtonClick(view:View) {
        (parentFragment as BaseTrailMapFragment<out ViewDataBinding>).expandInfoBottomSheet()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEditButtonClick(view:View) {
        (parentFragment as BaseTrailMapFragment<out ViewDataBinding>)
            .editTrailPoiInfo(this.trailPointOfInterestPosition!!)
    }
}
