package com.sildian.apps.togetrail.usecases.event

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.features.entities.event.EventUI
import com.sildian.apps.togetrail.repositories.auth.AuthRepository
import com.sildian.apps.togetrail.repositories.database.event.main.EventRepository
import com.sildian.apps.togetrail.usecases.mappers.toUIModel
import javax.inject.Inject
import kotlin.jvm.Throws

interface GetSingleEventUseCase {
    @Throws(AuthException::class, DatabaseException::class, IllegalStateException::class)
    suspend operator fun invoke(id: String): EventUI
}

class GetSingleEventUseCaseImpl @Inject constructor (
    private val authRepository: AuthRepository,
    private val eventRepository: EventRepository,
): GetSingleEventUseCase {

    override suspend operator fun invoke(id: String): EventUI =
        eventRepository
            .getEvent(id = id)
            .toUIModel(currentUserId = authRepository.getCurrentUser()?.id)
}