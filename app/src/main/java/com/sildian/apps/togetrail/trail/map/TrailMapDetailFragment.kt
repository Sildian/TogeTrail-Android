package com.sildian.apps.togetrail.trail.map

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.databinding.FragmentTrailMapDetailBinding
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Shows a specific trail on the map and allows to see all its detail information
 ************************************************************************************************/

@AndroidEntryPoint
class TrailMapDetailFragment: BaseTrailMapFragment<FragmentTrailMapDetailBinding>() {

    /***************************************Data*************************************************/

    val isTrailLikedByUser = MutableLiveData(false)
    val isTrailMarkedByUser = MutableLiveData(false)

    /************************************Data monitoring*****************************************/

    override fun loadData() {
        super.loadData()
        initializeData()
    }

    private fun initializeData() {
        this.binding.trailMapDetailFragment = this
        this.binding.trailViewModel = this.trailViewModel
        this.isTrailLikedByUser.value = CurrentHikerInfo.currentHikerLikedTrail.firstOrNull { trail ->
            trail.id == this.trailViewModel.data.value?.id
        } != null
        this.isTrailMarkedByUser.value = CurrentHikerInfo.currentHikerMarkedTrail.firstOrNull { trail ->
            trail.id == this.trailViewModel.data.value?.id
        } != null
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_detail

    override fun getMapViewId(): Int = R.id.fragment_trail_map_detail_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_detail_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_detail_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
    }

    override fun getMessageView(): View = this.binding.fragmentTrailMapDetailViewMessage

    override fun getMessageAnchorView(): View? = null

    @Suppress("UNUSED_PARAMETER")
    fun onSeeInfoButtonClick(view: View) {
        showTrailInfoFragment()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLikeButtonClick(view: View) {
        if (CurrentHikerInfo.currentHiker != null) {
            if (this.isTrailLikedByUser.value == true) {
                this.trailViewModel.unlikeTrail()
            } else {
                this.trailViewModel.likeTrail()
            }
            this.isTrailLikedByUser.value = !this.isTrailLikedByUser.value!!
        } else {
            hideInfoBottomSheet()
            SnackbarHelper
                .createSimpleSnackbar(
                    getMessageView(),
                    null,
                    R.string.message_account_necessary
                ).show()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMarkButtonClick(view: View) {
        if (CurrentHikerInfo.currentHiker != null) {
            if (this.isTrailMarkedByUser.value == true) {
                this.trailViewModel.unmarkTrail()
            } else {
                this.trailViewModel.markTrail()
            }
            this.isTrailMarkedByUser.value = !this.isTrailMarkedByUser.value!!
        } else {
            hideInfoBottomSheet()
            SnackbarHelper
                .createSimpleSnackbar(
                    getMessageView(),
                    null,
                    R.string.message_account_necessary
                ).show()
        }
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapReadyActionsFinished() {
        showTrailTrackOnMap()
    }

    override fun onMapClick(point: LatLng) {
        hideInfoBottomSheet()
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        /*Shows an info nested fragment depending on the tag of the marker*/

        return when (marker.tag) {
            is TrailPointOfInterest -> {
                marker.snippet?.let { poiPosition ->
                    showTrailPOIInfoFragment(poiPosition.toInt())
                    true
                }
                false
            }
            is TrailPoint -> {
                showTrailInfoFragment()
                true
            }
            else -> {
                false
            }
        }
    }

    /***********************************Trail monitoring*****************************************/

    override fun showTrailTrackOnMap() {

        if (this.trailViewModel.data.value != null) {

            super.showTrailTrackOnMap()

            /*Gets the first trailPoint*/

            val firstPoint = this.trailViewModel.data.value?.trailTrack?.getFirstTrailPoint()

            /*Moves the camera to the first point and zoom in*/

            if (firstPoint!=null) {
                this.map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(firstPoint.latitude, firstPoint.longitude), 14.0f))
            }
        }
    }
}
