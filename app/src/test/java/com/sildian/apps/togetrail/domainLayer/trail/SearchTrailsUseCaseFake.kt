package com.sildian.apps.togetrail.domainLayer.trail

import com.sildian.apps.togetrail.common.search.SearchRequest
import com.sildian.apps.togetrail.uiLayer.entities.trail.TrailUI
import com.sildian.apps.togetrail.uiLayer.entities.trail.nextTrailsUIList
import kotlin.random.Random

class SearchTrailsUseCaseFake(
    private val error: Throwable? = null,
    private val trails: List<TrailUI> = Random.nextTrailsUIList(),
) : SearchTrailsUseCase {

    override suspend operator fun invoke(request: SearchRequest): List<TrailUI> =
        error?.let { throw it } ?: trails
}