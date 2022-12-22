package com.sildian.apps.togetrail.trail.ui.map

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.google.android.gms.maps.model.LatLng
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationException
import com.sildian.apps.togetrail.common.utils.permissionsHelpers.PermissionsCallback
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.databinding.FragmentTrailMapRecordBinding
import com.sildian.apps.togetrail.trail.data.models.TrailPoint
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Lets the user record a trail in real time
 ************************************************************************************************/

@AndroidEntryPoint
class TrailMapRecordFragment: BaseTrailMapGenerateFragment<FragmentTrailMapRecordBinding>(), PermissionsCallback {

    /**********************************Static items**********************************************/

    companion object {
        private const val KEY_REQUEST_PERMISSION_NOTIFICATIONS = 2001
        private const val KEY_BUNDLE_PERMISSION_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS
    }

    /*************************************Service************************************************/

    private var trailRecordService: TrailRecordService? = null

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        startTrailRecordService()
        return this.layout
    }

    override fun onResume() {
        super.onResume()
        updateTrailTrack(this.trailRecordService?.getTrailPoints())
    }

    override fun onDestroyView() {
        stopRecord()
        stopObservingTrailPointsRegistered()
        stopTrailRecordService()
        super.onDestroyView()
    }

    /************************************Data monitoring*****************************************/

    override fun initializeData() {
        super.initializeData()
        this.binding.trailMapRecordFragment = this
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_record

    override fun getMapViewId(): Int = R.id.fragment_trail_map_record_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_record_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_record_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
        this.binding.fragmentTrailMapRecordButtonPoiAdd.isEnabled = true
        this.binding.fragmentTrailMapRecordButtonPlay.isEnabled = true
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
        this.binding.fragmentTrailMapRecordButtonPoiAdd.isEnabled = false
        this.binding.fragmentTrailMapRecordButtonPlay.isEnabled = false
    }

    override fun getMessageView(): View = this.binding.fragmentTrailMapRecordViewMessage

    override fun getMessageAnchorView(): View? = this.binding.fragmentTrailMapRecordButtonPlay

    @Suppress("UNUSED_PARAMETER")
    fun onSeeInfoButtonClick(view: View) {
        showTrailInfoFragment()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddPoiButtonClick(view: View) {
        addTrailPointOfInterest()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onPlayButtonClick(view: View) {
        if (this.trailRecordService?.isRecording() == true) {
            stopRecord()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationsPermission()
            } else {
                startRecord()
            }
        }
    }

    override fun revealActionsButtons(){
        this.binding.fragmentTrailMapRecordLayoutActionsButtons.visibility = View.VISIBLE
        this.binding.fragmentTrailMapRecordLayoutActionsButtons.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_appear_right)
        this.binding.fragmentTrailMapRecordLayoutActionsButtons.animate()
    }

    override fun hideActionsButtons() {

    }

    /***********************************Map monitoring*******************************************/

    override fun onMapClick(point: LatLng) {
        hideInfoBottomSheet()
    }

    /***********************************Trail monitoring*****************************************/

    private fun updateTrailTrack(trailPoints: List<TrailPoint>?) {
        if (!trailPoints.isNullOrEmpty()) {
            this.trailViewModel.data.value?.data?.trailTrack?.trailPoints?.clear()
            this.trailViewModel.data.value?.data?.trailTrack?.trailPoints?.addAll(trailPoints)
            showTrailTrackOnMap()
            if (this.trailViewModel.data.value?.data?.trailTrack?.trailPoints?.size == 1) {
                revealActionsButtons()
            }
        }
    }

    /***********************************Service monitoring***************************************/

    private fun startTrailRecordService() {
        Intent(baseActivity, TrailRecordService::class.java).also { intent ->
            baseActivity?.bindService(intent, trailRecordServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun stopTrailRecordService() {
        baseActivity?.unbindService(trailRecordServiceConnection)
    }

    private val trailRecordServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service != null && service is TrailRecordService.TrailRecordServiceBinder) {
                trailRecordService = service.getService()
                startObservingTrailPointsRegistered()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            trailRecordService = null
        }
    }

    /*************************************Record actions*****************************************/

    private fun startObservingTrailPointsRegistered() {
        this.trailRecordService?.startObservingTrailPointsRegistered(this) { dataHolder ->
            dataHolder.error?.let { e ->
                if (e is UserLocationException) {
                    handleUserLocationError(e)
                } else {
                    onQueryError(e)
                }
            } ?:
            updateTrailTrack(dataHolder.data)
        }
    }

    private fun stopObservingTrailPointsRegistered() {
        this.trailRecordService?.stopObservingTrailPointsRegistered(this)
    }

    private fun startRecord() {
        this.binding.fragmentTrailMapRecordButtonPlay.setImageResource(R.drawable.ic_record_pause_white)
        this.trailRecordService?.startRecord()
    }

    private fun stopRecord() {
        this.binding.fragmentTrailMapRecordButtonPlay.setImageResource(R.drawable.ic_record_play_white)
        this.trailRecordService?.stopRecord()
    }

    private fun handleUserLocationError(e: UserLocationException?) {
        e?.errorCode?.let { errorCode ->
            stopRecord()
            when (errorCode) {
                UserLocationException.ErrorCode.ACCESS_NOT_GRANTED -> {
                    //TODO prompt an appropriate action ?
                    SnackbarHelper
                        .createSimpleSnackbar(
                            this.binding.fragmentTrailMapRecordViewMessage,
                            this.binding.fragmentTrailMapRecordButtonPlay,
                            R.string.message_user_location_failure
                        ).show()
                }
                UserLocationException.ErrorCode.GPS_UNAVAILABLE -> {
                    SnackbarHelper.createSnackbarWithAction(
                        this.binding.fragmentTrailMapRecordViewMessage,
                        this.binding.fragmentTrailMapRecordButtonPlay,
                        R.string.message_device_gps_unavailable,
                        R.string.button_common_activate
                    ) {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }.show()
                }
                UserLocationException.ErrorCode.ERROR_UNKNOWN -> {
                    SnackbarHelper
                        .createSimpleSnackbar(
                            this.binding.fragmentTrailMapRecordViewMessage,
                            this.binding.fragmentTrailMapRecordButtonPlay,
                            R.string.message_user_location_failure
                        ).show()
                }
            }
        }
    }

    /***********************************Permissions**********************************************/

    private fun requestNotificationsPermission() {
        baseActivity?.requestPermissions(
            KEY_REQUEST_PERMISSION_NOTIFICATIONS,
            arrayOf(KEY_BUNDLE_PERMISSION_NOTIFICATIONS),
            this,
            R.string.message_permission_requested_message_notifications
        )
    }

    override fun onPermissionsGranted(permissionsRequestCode: Int) {
        startRecord()
    }

    override fun onPermissionsDenied(permissionsRequestCode: Int) {
        startRecord()
    }
}
