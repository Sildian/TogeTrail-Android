package com.sildian.apps.togetrail.trail.info

import android.view.View
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.FragmentTrailPoiInfoBinding
import com.sildian.apps.togetrail.trail.map.BaseTrailMapFragment
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel

/*************************************************************************************************
 * Shows information about a point of interest
 * This fragment should be used as a nested fragment within a BottomSheet
 * @param trailViewModel : the trail data
 * @param trailPointOfInterestPosition : the position of the trailPointOfInterest in the trailTrack
 * @param isEditable : true if the info can be edited
 ************************************************************************************************/

class TrailPOIInfoFragment (
    private val trailViewModel: TrailViewModel?=null,
    private val trailPointOfInterestPosition:Int?=null,
    private val isEditable:Boolean=false
)
    : BaseInfoFragment() {

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
        this.binding.lifecycleOwner = this
        (this.binding as FragmentTrailPoiInfoBinding).trailPOIInfoFragment = this
        (this.binding as FragmentTrailPoiInfoBinding).trailViewModel = this.trailViewModel
        (this.binding as FragmentTrailPoiInfoBinding).isEditable = this.isEditable
    }

    private fun observeTrailPOI() {
        this.trailViewModel?.trailPointOfInterest?.observe(this) { trailPOI ->
            if (trailPOI != null) {
                refreshUI()
            }
        }
    }

    private fun observeRequestFailure() {
        this.trailViewModel?.requestFailure?.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_poi_info

    override fun useDataBinding(): Boolean = true

    override fun getTopViewId(): Int = R.id.fragment_trail_poi_info_image_view_photo

    override fun getBottomViewId(): Int = R.id.fragment_trail_poi_info_layout_info

    @Suppress("UNUSED_PARAMETER")
    fun onSeeButtonClick(view:View) {
        (parentFragment as BaseTrailMapFragment).expandInfoBottomSheet()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEditButtonClick(view:View) {
        (parentFragment as BaseTrailMapFragment)
            .editTrailPoiInfo(this.trailPointOfInterestPosition!!)
    }
}
