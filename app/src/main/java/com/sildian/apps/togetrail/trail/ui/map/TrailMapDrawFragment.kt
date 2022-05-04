package com.sildian.apps.togetrail.trail.ui.map

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.FragmentTrailMapDrawBinding
import com.sildian.apps.togetrail.trail.data.core.TrailPoint
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Lets the user create a trail by drawing the track on the map and adding points of interest
 ************************************************************************************************/

@AndroidEntryPoint
class TrailMapDrawFragment : BaseTrailMapGenerateFragment<FragmentTrailMapDrawBinding>() {

    /************************************Data monitoring*****************************************/

    override fun loadData() {
        super.loadData()
        initializeData()
    }

    private fun initializeData() {
        this.binding.trailMapDrawFragment = this
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_draw

    override fun getMapViewId(): Int = R.id.fragment_trail_map_draw_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_draw_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_draw_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
        this.binding.fragmentTrailMapDrawButtonPointRemove.isEnabled = true
        this.binding.fragmentTrailMapDrawButtonPoiAdd.isEnabled = true
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
        this.binding.fragmentTrailMapDrawButtonPointRemove.isEnabled = false
        this.binding.fragmentTrailMapDrawButtonPoiAdd.isEnabled = false
    }

    override fun getMessageView(): View = this.binding.fragmentTrailMapDrawViewMessage

    override fun getMessageAnchorView(): View? = null

    @Suppress("UNUSED_PARAMETER")
    fun onSeeInfoButtonClick(view: View) {
        showTrailInfoFragment()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRemovePointButtonClick(view: View) {
        removeLastTrailPoint()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddPoiButtonClick(view: View) {
        addTrailPointOfInterest()
    }

    override fun revealActionsButtons() {
        this.binding.fragmentTrailMapDrawLayoutActionsButtons.visibility = View.VISIBLE
        this.binding.fragmentTrailMapDrawLayoutActionsButtons.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_appear_right)
        this.binding.fragmentTrailMapDrawLayoutActionsButtons.animate()
    }

    override fun hideActionsButtons() {
        val hideAnimation=AnimationUtils.loadAnimation(context, R.anim.vanish_down)
        hideAnimation.setAnimationListener(object:Animation.AnimationListener{
            override fun onAnimationRepeat(anim: Animation?) {

            }

            override fun onAnimationEnd(anim: Animation?) {
                binding.fragmentTrailMapDrawLayoutActionsButtons.visibility=View.GONE
            }

            override fun onAnimationStart(anim: Animation?) {

            }
        })
        this.binding.fragmentTrailMapDrawLayoutActionsButtons.startAnimation(hideAnimation)
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapClick (point: LatLng) {
        if (this.infoBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
            hideInfoBottomSheet()
        }
        val trailPoint = TrailPoint(point.latitude, point.longitude)
        addTrailPoint(trailPoint)
    }
}
