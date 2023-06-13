package com.sildian.apps.togetrail.usecases.event

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.nextUser
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.repositories.auth.AuthRepository
import com.sildian.apps.togetrail.repositories.auth.AuthRepositoryFake
import com.sildian.apps.togetrail.repositories.database.entities.event.nextEvent
import com.sildian.apps.togetrail.repositories.database.event.main.EventRepository
import com.sildian.apps.togetrail.repositories.database.event.main.EventRepositoryFake
import com.sildian.apps.togetrail.usecases.mappers.toUIModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GetSingleEventUseCaseImplTest {

    private fun initUseCase(
        authRepository: AuthRepository = AuthRepositoryFake(),
        eventRepository: EventRepository = EventRepositoryFake(),
    ): GetSingleEventUseCase = GetSingleEventUseCaseImpl(
        authRepository = authRepository,
        eventRepository = eventRepository,
    )

    @Test(expected = AuthException::class)
    fun `GIVEN AuthException error WHEN invoke THEN throws AuthException`() = runTest {
        // Given
        val useCase = initUseCase(
            authRepository = AuthRepositoryFake(error = AuthException.UnknownException())
        )

        // When
        useCase(id = Random.nextString())
    }

    @Test(expected = DatabaseException::class)
    fun `GIVEN DatabaseException error WHEN invoke THEN throws DatabaseException`() = runTest {
        // Given
        val useCase = initUseCase(
            eventRepository = EventRepositoryFake(error = DatabaseException.UnknownException())
        )

        // When
        useCase(id = Random.nextString())
    }

    @Test
    fun `GIVEN Event WHEN invoke THEN return correct EventUI`() = runTest {
        // Given
        val currentUser = Random.nextUser()
        val event = Random.nextEvent()
        val useCase = initUseCase(
            authRepository = AuthRepositoryFake(currentUser = currentUser),
            eventRepository = EventRepositoryFake(events = listOf(event)),
        )

        // When
        val eventUI = useCase(id = Random.nextString())

        // Then
        val expectedEventUI = event.toUIModel(currentUserId = currentUser.id)
        assertEquals(expectedEventUI, eventUI)
    }
}