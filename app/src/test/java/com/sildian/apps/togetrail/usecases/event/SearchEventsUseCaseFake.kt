package com.sildian.apps.togetrail.usecases.event

import com.sildian.apps.togetrail.common.search.SearchRequest
import com.sildian.apps.togetrail.features.entities.event.EventUI
import com.sildian.apps.togetrail.features.entities.event.nextEventsUIList
import kotlin.random.Random

class SearchEventsUseCaseFake(
    private val error: Throwable? = null,
    private val events: List<EventUI> = Random.nextEventsUIList(),
) : SearchEventsUseCase {

    override suspend operator fun invoke(request: SearchRequest): List<EventUI> =
        error?.let { throw it } ?: events
}