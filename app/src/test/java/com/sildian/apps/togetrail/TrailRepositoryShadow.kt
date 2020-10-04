package com.sildian.apps.togetrail

import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data requester tests
 ************************************************************************************************/

@Implements(TrailRepository::class)
class TrailRepositoryShadow {

    @Implementation
    suspend fun getTrail(trailId: String): Trail? {
        println("FAKE TrailRepository : Get trail")
        return BaseDataRequesterTest.getTrailSample()
    }

    @Implementation
    suspend fun addTrail(trail: Trail): String? {
        println("FAKE TrailRepository : Add trail")
        BaseDataRequesterTest.isTrailAdded = true
        return BaseDataRequesterTest.TRAIL_ID
    }

    @Implementation
    suspend fun updateTrail(trail: Trail) {
        println("FAKE TrailRepository : Update trail")
        BaseDataRequesterTest.isTrailUpdated = true
    }
}