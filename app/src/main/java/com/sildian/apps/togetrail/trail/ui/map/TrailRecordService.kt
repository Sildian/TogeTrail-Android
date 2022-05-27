package com.sildian.apps.togetrail.trail.ui.map

import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseViewModels.ListDataHolder
import com.sildian.apps.togetrail.common.utils.uiHelpers.NotificationHelper
import com.sildian.apps.togetrail.trail.data.models.TrailPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/*************************************************************************************************
 * This Service aims to record a Trail in real time using the user location
 ************************************************************************************************/

@AndroidEntryPoint
class TrailRecordService : LifecycleService() {

    companion object {

        /**Notification items**/
        private const val NOTIFICATION_CHANNEL_ID = "com.sildian.apps.togetrail.notificationChannel.trailRecord"
        private const val NOTIFICATION_ID = 1000
    }

    /***************************************Binder***********************************************/

    private val binder = TrailRecordServiceBinder()

    inner class TrailRecordServiceBinder: Binder() {
        fun getService(): TrailRecordService = this@TrailRecordService
    }

    /***********************************Notification*********************************************/

    private lateinit var notification: Notification

    /*************************************Executor***********************************************/

    @Inject lateinit var trailRecordExecutor: TrailRecordExecutor

    /***********************************Life cycle***********************************************/

    override fun onBind(intent: Intent): IBinder = this.binder

    override fun onCreate() {
        super.onCreate()
        this.notification = NotificationHelper.createLowPriorityNotification(
            this,
            R.string.message_trail_recording_title,
            R.string.message_trail_recording_message,
            NOTIFICATION_CHANNEL_ID
        )
    }

    /*************************************Record actions*****************************************/

    fun startObservingTrailPointsRegistered(lifeCycleOwner: LifecycleOwner, trailPointsObserver: Observer<ListDataHolder<TrailPoint>>) {
        trailRecordExecutor.trailPointsRegistered.observe(lifeCycleOwner, trailPointsObserver)
    }

    fun stopObservingTrailPointsRegistered(lifeCycleOwner: LifecycleOwner) {
        trailRecordExecutor.trailPointsRegistered.removeObservers(lifeCycleOwner)
    }

    fun startRecord() {
        lifecycleScope.launch {
            if (trailRecordExecutor.isRecording()) {
                stopRecord()
            }
            startForeground(NOTIFICATION_ID, notification)
            trailRecordExecutor.start()
        }
    }

    fun stopRecord() {
        lifecycleScope.launch {
            trailRecordExecutor.stop()
            stopForeground(true)
        }
    }

    fun isRecording(): Boolean = this.trailRecordExecutor.isRecording()

    fun getTrailPoints(): List<TrailPoint>? = this.trailRecordExecutor.trailPointsRegistered.value?.data
}