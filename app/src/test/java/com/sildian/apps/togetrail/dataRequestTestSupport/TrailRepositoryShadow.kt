package com.sildian.apps.togetrail.dataRequestTestSupport

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data request tests
 ************************************************************************************************/

@Implements(TrailRepository::class)
class TrailRepositoryShadow {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE TrailRepository : Request failure"
    }

    @Implementation
    suspend fun getTrail(trailId: String): Trail? {
        println("FAKE TrailRepository : Get trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.trails.firstOrNull { it.id == trailId }
    }

    @Implementation
    suspend fun addTrail(trail: Trail): String? {
        println("FAKE TrailRepository : Add trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        trail.id = "TNEW"
        FirebaseSimulator.trails.add(trail)
        return trail.id
    }

    @Implementation
    suspend fun updateTrail(trail: Trail) {
        println("FAKE TrailRepository : Update trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.trails.firstOrNull { it.id == trail.id }?.let { existingTrail ->
            FirebaseSimulator.trails.remove(existingTrail)
            FirebaseSimulator.trails.add(trail)
        }
    }
}