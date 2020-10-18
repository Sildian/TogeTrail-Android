package com.sildian.apps.togetrail.trail.map

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.exceptions.UserLocationException
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import kotlinx.android.synthetic.main.fragment_trail_map_record.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Lets the user record a trail in real time
 ************************************************************************************************/

class TrailMapRecordFragment(trailViewModel: TrailViewModel)
    : BaseTrailMapGenerateFragment(trailViewModel) {

    /*************************************Executor***********************************************/

    private var trailRecordExecutor: TrailRecordExecutor? = null    //TODO what if screen orientation changed ?

    /**********************************UI component**********************************************/

    private val seeInfoButton by lazy {layout.fragment_trail_map_record_button_info_see}
    private val actionsButtonsLayout by lazy {layout.fragment_trail_map_record_layout_actions_buttons}
    private val addPoiButton by lazy {layout.fragment_trail_map_record_button_poi_add}
    private val playButton by lazy {layout.fragment_trail_map_record_button_play}
    private val messageView by lazy {layout.fragment_trail_map_record_view_message}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initTrailRecordExecutor()
        return this.layout
    }

    override fun onResume() {
        super.onResume()
        updateTrailTrack(this.trailRecordExecutor?.trailPointsRegistered)
    }

    override fun onDestroyView() {
        stopRecord()
        super.onDestroyView()
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_record

    override fun useDataBinding(): Boolean = false

    override fun getMapViewId(): Int = R.id.fragment_trail_map_record_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_record_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_record_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
        this.addPoiButton.isEnabled=true
        this.playButton.isEnabled=true
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
        this.addPoiButton.isEnabled=false
        this.playButton.isEnabled=false
    }

    override fun getMessageView(): View = this.messageView

    override fun getMessageAnchorView(): View? = this.playButton

    override fun initializeUI() {
        initializeSeeInfoButton()
        initializeAddPoiButton()
        initializePlayButton()
        super.initializeUI()
    }

    private fun initializeSeeInfoButton(){
        this.seeInfoButton.setOnClickListener {
            showTrailInfoFragment()
        }
    }

    private fun initializeAddPoiButton(){
        this.addPoiButton.setOnClickListener {
            addTrailPointOfInterest()
        }
    }

    private fun initializePlayButton(){
        this.playButton.setOnClickListener {
            when (this.trailRecordExecutor?.isRecording()) {
                false -> startRecord()
                true -> stopRecord()
            }
        }
    }

    override fun revealActionsButtons(){
        this.actionsButtonsLayout.visibility=View.VISIBLE
        this.actionsButtonsLayout.layoutAnimation=
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_appear_right)
        this.actionsButtonsLayout.animate()
    }

    override fun hideActionsButtons() {

    }

    /***********************************Map monitoring*******************************************/

    override fun onMapClick(point: LatLng?) {
        hideInfoBottomSheet()
    }

    /***********************************Trail monitoring*****************************************/

    private fun updateTrailTrack(trailPoints: List<TrailPoint>?) {
        if (!trailPoints.isNullOrEmpty()) {
            this.trailViewModel?.trail?.trailTrack?.trailPoints?.clear()
            this.trailViewModel?.trail?.trailTrack?.trailPoints?.addAll(trailPoints)
            showTrailTrackOnMap()
            if(this.trailViewModel?.trail?.trailTrack?.trailPoints?.size==1) {
                revealActionsButtons()
            }
        }
    }

    /*************************************Record actions*****************************************/

    private fun initTrailRecordExecutor() {
        context?.let { context ->
            this.trailRecordExecutor = TrailRecordExecutor(context)
            observeTrailRecordPoint()
            observeTrailRecordFailure()
        }
    }

    private fun observeTrailRecordPoint() {
        this.trailRecordExecutor?.trailPointsRegisteredLiveData?.observe(this, { trailPoints ->
            updateTrailTrack(trailPoints)
        })
    }

    private fun observeTrailRecordFailure() {
        this.trailRecordExecutor?.userLocationFailureLiveData?.observe(this, { e ->
            e?.errorCode?.let { errorCode ->
                stopRecord()
                when (errorCode) {
                    UserLocationException.ErrorCode.ACCESS_NOT_GRANTED -> {
                        //TODO prompt an appropriate action ?
                        SnackbarHelper
                            .createSimpleSnackbar(
                                this.messageView,
                                this.playButton,
                                R.string.message_user_location_failure
                            ).show()
                    }
                    UserLocationException.ErrorCode.GPS_UNAVAILABLE -> {
                        SnackbarHelper.createSnackbarWithAction(
                            this.messageView,
                            this.playButton,
                            R.string.message_device_gps_unavailable,
                            R.string.button_common_activate
                        ) {
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }.show()
                    }
                    UserLocationException.ErrorCode.ERROR_UNKNOWN -> {
                        SnackbarHelper
                            .createSimpleSnackbar(
                                this.messageView,
                                this.playButton,
                                R.string.message_user_location_failure
                            ).show()
                    }
                }
            }
        })
    }

    private fun startRecord() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                playButton.setImageResource(R.drawable.ic_record_pause_white)
                trailRecordExecutor?.start()
            }
        }
    }

    private fun stopRecord() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                playButton.setImageResource(R.drawable.ic_record_play_white)
                trailRecordExecutor?.stop()
            }
        }
    }
}
