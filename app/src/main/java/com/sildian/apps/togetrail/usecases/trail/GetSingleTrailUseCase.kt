package com.sildian.apps.togetrail.usecases.trail

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.features.entities.trail.TrailUI
import com.sildian.apps.togetrail.repositories.auth.AuthRepository
import com.sildian.apps.togetrail.repositories.database.trail.main.TrailRepository
import com.sildian.apps.togetrail.usecases.mappers.toUIModel
import javax.inject.Inject
import kotlin.jvm.Throws

interface GetSingleTrailUseCase {
    @Throws(AuthException::class, DatabaseException::class, IllegalStateException::class)
    suspend operator fun invoke(id: String): TrailUI
}

class GetSingleTrailUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val trailRepository: TrailRepository,
) : GetSingleTrailUseCase {

    override suspend operator fun invoke(id: String): TrailUI =
        trailRepository
            .getTrail(id = id)
            .toUIModel(currentUserId = authRepository.getCurrentUser()?.id)
}