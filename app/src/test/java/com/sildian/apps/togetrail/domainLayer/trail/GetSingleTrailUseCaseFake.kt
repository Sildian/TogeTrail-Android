package com.sildian.apps.togetrail.domainLayer.trail

import com.sildian.apps.togetrail.uiLayer.entities.trail.TrailUI
import com.sildian.apps.togetrail.uiLayer.entities.trail.nextTrailUI
import kotlin.random.Random

class GetSingleTrailUseCaseFake(
    private val error: Throwable? = null,
    private val trail: TrailUI = Random.nextTrailUI(),
) : GetSingleTrailUseCase {

    override suspend operator fun invoke(id: String): TrailUI =
        error?.let { throw it } ?: trail
}