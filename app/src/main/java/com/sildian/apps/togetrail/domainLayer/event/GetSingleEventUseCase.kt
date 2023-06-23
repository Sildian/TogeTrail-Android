package com.sildian.apps.togetrail.domainLayer.event

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.uiLayer.entities.event.EventUI
import com.sildian.apps.togetrail.dataLayer.auth.AuthRepository
import com.sildian.apps.togetrail.dataLayer.database.event.main.EventRepository
import com.sildian.apps.togetrail.domainLayer.mappers.toUIModel
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