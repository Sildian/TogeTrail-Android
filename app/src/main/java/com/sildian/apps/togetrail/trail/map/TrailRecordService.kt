package com.sildian.apps.togetrail.trail.map

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationException
import com.sildian.apps.togetrail.common.utils.uiHelpers.NotificationHelper
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*************************************************************************************************
 * This Service aims to record a Trail in real time using the user location
 ************************************************************************************************/

@AndroidEntryPoint
class TrailRecordService : Service() {

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

    fun startRecord(lifeCycleOwner: LifecycleOwner,
                    trailPointsObserver: Observer<List<TrailPoint>?>, errorObserver: Observer<UserLocationException?>)
    {
        if (this.trailRecordExecutor.isRecording) {
            stopRecord(lifeCycleOwner)
        }
        startForeground(NOTIFICATION_ID, notification)
        this.trailRecordExecutor.trailPointsRegisteredLiveData.observe(lifeCycleOwner, trailPointsObserver)
        this.trailRecordExecutor.userLocationFailureLiveData.observe(lifeCycleOwner, errorObserver)
        this.trailRecordExecutor.start()
    }

    fun stopRecord(lifeCycleOwner: LifecycleOwner) {
        this.trailRecordExecutor.trailPointsRegisteredLiveData.removeObservers(lifeCycleOwner)
        this.trailRecordExecutor.userLocationFailureLiveData.removeObservers(lifeCycleOwner)
        this.trailRecordExecutor.stop()
        stopForeground(true)
    }

    fun isRecording(): Boolean = this.trailRecordExecutor.isRecording

    fun getTrailPoints(): List<TrailPoint> = this.trailRecordExecutor.trailPointsRegistered
}