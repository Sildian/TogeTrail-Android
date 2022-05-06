package com.sildian.apps.togetrail.trail.ui.info

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.FragmentTrailPoiInfoBinding
import com.sildian.apps.togetrail.trail.ui.map.BaseTrailMapFragment
import com.sildian.apps.togetrail.trail.data.viewModels.TrailViewModel
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Shows information about a point of interest
 * This fragment should be used as a nested fragment within a BottomSheet
 * @param trailPointOfInterestPosition : the position of the trailPointOfInterest in the trailTrack
 * @param isEditable : true if the info can be edited
 ************************************************************************************************/

@AndroidEntryPoint
class TrailPOIInfoFragment (
    private val trailPointOfInterestPosition:Int? = null,
    private val isEditable:Boolean=false
)
    : BaseInfoFragment<FragmentTrailPoiInfoBinding>() {

    /**************************************Data**************************************************/

    private val trailViewModel: TrailViewModel by activityViewModels()

    /*********************************Data monitoring********************************************/

    override fun initializeData() {
        this.trailPointOfInterestPosition?.let { position ->
            this.trailViewModel.watchPointOfInterest(position)
        }
        this.binding.trailPOIInfoFragment = this
        this.binding.trailViewModel = this.trailViewModel
        this.binding.isEditable = this.isEditable
        observeTrailPOI()
    }

    private fun observeTrailPOI() {
        this.trailViewModel.trailPointOfInterest.observe(this) { trailPOIData ->
            trailPOIData?.error?.let { e ->
                onQueryError(e)
            } ?:
            trailPOIData?.data?.let { trailPOI ->
                refreshUI()
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
