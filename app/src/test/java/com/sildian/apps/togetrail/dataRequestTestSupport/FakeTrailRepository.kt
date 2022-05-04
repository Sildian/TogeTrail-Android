package com.sildian.apps.togetrail.dataRequestTestSupport

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.trail.data.core.Trail
import com.sildian.apps.togetrail.trail.data.source.TrailRepository
import org.mockito.Mockito

/*************************************************************************************************
 * Fake repository for Trail
 ************************************************************************************************/

class FakeTrailRepository: TrailRepository {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE TrailRepository : Request failure"
    }

    override fun getTrailReference(trailId: String): DocumentReference =
        Mockito.mock(DocumentReference::class.java)

    override suspend fun getTrail(trailId: String): Trail? {
        println("FAKE TrailRepository : Get trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.trails.firstOrNull { it.id == trailId }
    }

    override suspend fun addTrail(trail: Trail): String? {
        println("FAKE TrailRepository : Add trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        trail.id = "TNEW"
        FirebaseSimulator.trails.add(trail)
        return trail.id
    }

    override suspend fun updateTrail(trail: Trail) {
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