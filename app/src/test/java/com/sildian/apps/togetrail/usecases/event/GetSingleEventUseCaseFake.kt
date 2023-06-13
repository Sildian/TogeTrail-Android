package com.sildian.apps.togetrail.usecases.event

import com.sildian.apps.togetrail.features.entities.event.EventUI
import com.sildian.apps.togetrail.features.entities.event.nextEventUI
import kotlin.random.Random

class GetSingleEventUseCaseFake(
    private val error: Throwable? = null,
    private val event: EventUI = Random.nextEventUI(),
) : GetSingleEventUseCase {

    override suspend operator fun invoke(id: String): EventUI =
        error?.let { throw it } ?: event
}