package com.sildian.apps.togetrail

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data requester tests
 ************************************************************************************************/

@Implements(TrailRepository::class)
class TrailRepositoryShadow {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE TrailRepository : Request failure"
    }

    @Implementation
    suspend fun getTrail(trailId: String): Trail? {
        println("FAKE TrailRepository : Get trail")
        if (!BaseDataRequesterTest.requestShouldFail) {
            return BaseDataRequesterTest.getTrailSample()
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun addTrail(trail: Trail): String? {
        println("FAKE TrailRepository : Add trail")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isTrailAdded = true
            return BaseDataRequesterTest.TRAIL_ID
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun updateTrail(trail: Trail) {
        println("FAKE TrailRepository : Update trail")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isTrailUpdated = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }
}