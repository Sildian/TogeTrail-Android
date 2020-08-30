package com.sildian.apps.togetrail.trail.map

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import kotlinx.android.synthetic.main.fragment_trail_map_draw.view.*

/*************************************************************************************************
 * Lets the user create a trail by drawing the track on the map and adding points of interest
 ************************************************************************************************/

class TrailMapDrawFragment(trailViewModel: TrailViewModel)
    : BaseTrailMapGenerateFragment(trailViewModel){

    /**********************************UI component**********************************************/

    private val seeInfoButton by lazy {layout.fragment_trail_map_draw_button_info_see}
    private val actionsButtonsLayout by lazy {layout.fragment_trail_map_draw_layout_actions_buttons}
    private val removePointButton by lazy {layout.fragment_trail_map_draw_button_point_remove}
    private val addPoiButton by lazy {layout.fragment_trail_map_draw_button_poi_add}
    private val messageView by lazy { layout.fragment_trail_map_draw_view_message }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_draw

    override fun useDataBinding(): Boolean = false

    override fun getMapViewId(): Int = R.id.fragment_trail_map_draw_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_draw_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_draw_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
        this.removePointButton.isEnabled=true
        this.addPoiButton.isEnabled=true
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
        this.removePointButton.isEnabled=false
        this.addPoiButton.isEnabled=false
    }

    override fun getMessageView(): View = this.messageView

    override fun getMessageAnchorView(): View? = null

    override fun initializeUI() {
        initializeSeeInfoButton()
        initializeRemovePointButton()
        initializeAddPoiButton()
        super.initializeUI()
    }

    private fun initializeSeeInfoButton(){
        this.seeInfoButton.setOnClickListener {
            showTrailInfoFragment()
        }
    }

    private fun initializeRemovePointButton(){
        this.removePointButton.setOnClickListener {
            removeLastTrailPoint()
        }
    }

    private fun initializeAddPoiButton(){
        this.addPoiButton.setOnClickListener {
            addTrailPointOfInterest()
        }
    }

    override fun revealActionsButtons(){
        this.actionsButtonsLayout.visibility=View.VISIBLE
        this.actionsButtonsLayout.layoutAnimation=
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_appear_right)
        this.actionsButtonsLayout.animate()
    }

    override fun hideActionsButtons(){
        val hideAnimation=AnimationUtils.loadAnimation(context, R.anim.vanish_down)
        hideAnimation.setAnimationListener(object:Animation.AnimationListener{
            override fun onAnimationRepeat(anim: Animation?) {

            }

            override fun onAnimationEnd(anim: Animation?) {
                actionsButtonsLayout.visibility=View.GONE
            }

            override fun onAnimationStart(anim: Animation?) {

            }
        })
        this.actionsButtonsLayout.startAnimation(hideAnimation)
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapClick(point: LatLng?) {
        if (this.infoBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
            hideInfoBottomSheet()
        }
        if (point != null) {
            val trailPoint =
                TrailPoint(
                    point.latitude,
                    point.longitude
                )
            addTrailPoint(trailPoint)
        }
    }
}
