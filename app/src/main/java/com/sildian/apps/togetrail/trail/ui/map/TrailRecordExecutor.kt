package com.sildian.apps.togetrail.trail.ui.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.sildian.apps.togetrail.common.baseDataRequests.DataFlowRequest
import com.sildian.apps.togetrail.common.baseViewModels.ListDataHolder
import com.sildian.apps.togetrail.common.utils.GeoUtilities
import com.sildian.apps.togetrail.common.utils.coroutinesHelpers.CoroutineIODispatcher
import com.sildian.apps.togetrail.common.utils.locationHelpers.UpdateUserLocationDataFlowRequest
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationContinuousFinder
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationException
import com.sildian.apps.togetrail.trail.data.models.TrailPoint
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

/*************************************************************************************************
 * Records a Trail in real time using the user location
 ************************************************************************************************/

@ServiceScoped
class TrailRecordExecutor @Inject constructor(
    @CoroutineIODispatcher val dispatcher: CoroutineDispatcher,
    private val userLocationContinuousFinder: UserLocationContinuousFinder
) {

    companion object {

        /**Logs**/
        private const val TAG = "TrailRecordExecutor"

        /**Record data**/
        //The time interval between each point record
        private const val VALUE_RECORD_TIME_INTERVAL_MILLIS = 5000
        //The minimum required distance between two points to check before register
        private const val VALUE_RECORD_MIN_DISTANCE_METERS = 2
    }

    /*************************************Data**************************************************/

    private var dataRequest: DataFlowRequest<Location>? = null
    private val mutableTrailPointsRegistered = MutableLiveData<ListDataHolder<TrailPoint>>()
    val trailPointsRegistered: LiveData<ListDataHolder<TrailPoint>> = mutableTrailPointsRegistered

    /*********************************Data monitoring********************************************/

    fun isRecording(): Boolean = this.dataRequest?.isRunning()?: false

    /***********************************Job monitoring*******************************************/

    suspend fun start(nbResultsToGet: Int? = null) {
        stop()
        this.dataRequest = UpdateUserLocationDataFlowRequest(
            this.dispatcher,
            this.userLocationContinuousFinder,
            VALUE_RECORD_TIME_INTERVAL_MILLIS.toLong()
        )
        this.dataRequest?.execute()
        val flow = nbResultsToGet?.let { this.dataRequest?.flow?.take(nbResultsToGet) }?: this.dataRequest?.flow
        flow
            ?.catch { e ->
                e.printStackTrace()
                Log.e(TAG, "User location cannot be reached : ${e.message}")
                mutableTrailPointsRegistered.postValue(
                    ListDataHolder(mutableTrailPointsRegistered.value?.data ?: emptyList(), e)
                )
            }
            ?.collect { result ->
                result?.let { location ->
                    handleUserLocation(location)
                } ?: run {
                    val e = UserLocationException(UserLocationException.ErrorCode.ERROR_UNKNOWN)
                    Log.e(TAG, "User location cannot be reached : ${e.message}")
                    mutableTrailPointsRegistered.postValue(
                        ListDataHolder(mutableTrailPointsRegistered.value?.data ?: emptyList(), e)
                    )
                }
            }
    }

    suspend fun stop() {
        this.dataRequest?.cancel()
    }

    /***********************************Trail recording*******************************************/

    private fun handleUserLocation(userLocation: Location) {
        if (checkMinDistanceToPreviousPointIsFulfilled(userLocation)) {
            Log.d(TAG, "Point registered at lat ${userLocation.latitude} lng ${userLocation.longitude}")
            val trailPoint = TrailPoint(
                userLocation.latitude,
                userLocation.longitude,
                userLocation.altitude.toInt(),
                Date()
            )
            val trailPointsList = arrayListOf<TrailPoint>()
            trailPointsRegistered.value?.data?.let {
                trailPointsList.addAll(it)
            }
            trailPointsList.add(trailPoint)
            mutableTrailPointsRegistered.postValue(ListDataHolder(trailPointsList))
        } else {
            Log.d(TAG, "Point not registered, too closed to the previous point")
        }
    }

    private fun checkMinDistanceToPreviousPointIsFulfilled(location: Location): Boolean {
        this.trailPointsRegistered.value?.data?.let { trailPointsRegistered ->
            trailPointsRegistered.lastOrNull()?.let { previousPoint ->
                val pointA = LatLng(location.latitude, location.longitude)
                val pointB = LatLng(previousPoint.latitude, previousPoint.longitude)
                return GeoUtilities.getDistance(pointA, pointB) >= VALUE_RECORD_MIN_DISTANCE_METERS
            }
        }
        return true
    }
}