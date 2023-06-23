package com.sildian.apps.togetrail.domainLayer.event

import com.sildian.apps.togetrail.uiLayer.entities.event.EventUI
import com.sildian.apps.togetrail.uiLayer.entities.event.nextEventUI
import kotlin.random.Random

class GetSingleEventUseCaseFake(
    private val error: Throwable? = null,
    private val event: EventUI = Random.nextEventUI(),
) : GetSingleEventUseCase {

    override suspend operator fun invoke(id: String): EventUI =
        error?.let { throw it } ?: event
}