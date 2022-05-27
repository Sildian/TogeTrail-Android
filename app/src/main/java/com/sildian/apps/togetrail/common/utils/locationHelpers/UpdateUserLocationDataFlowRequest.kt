package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.sildian.apps.togetrail.common.baseDataRequests.DataFlowRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/*************************************************************************************************
 * Periodically updates the user location
 ************************************************************************************************/

@ExperimentalCoroutinesApi
class UpdateUserLocationDataFlowRequest(
    dispatcher: CoroutineDispatcher,
    private val userLocationContinuousFinder: UserLocationContinuousFinder,
    private val intervalMillis: Long
)
    : DataFlowRequest<Location>(dispatcher)
{

    override fun provideFlow(): Flow<Location?> =
        callbackFlow {
            val locationCallback = object: LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        trySend(location)
                    }
                }
            }
            try {
                userLocationContinuousFinder.startLocationUpdates(intervalMillis, locationCallback)
            } catch (e: UserLocationException) {
                close(e)
            }
            awaitClose { userLocationContinuousFinder.stopLocationUpdates(locationCallback) }
        }
}