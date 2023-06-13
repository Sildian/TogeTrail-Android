package com.sildian.apps.togetrail.usecases.trail

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.nextUser
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.repositories.auth.AuthRepository
import com.sildian.apps.togetrail.repositories.auth.AuthRepositoryFake
import com.sildian.apps.togetrail.repositories.database.entities.trail.nextTrail
import com.sildian.apps.togetrail.repositories.database.trail.main.TrailRepository
import com.sildian.apps.togetrail.repositories.database.trail.main.TrailRepositoryFake
import com.sildian.apps.togetrail.usecases.mappers.toUIModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GetSingleTrailUseCaseImplTest {

    private fun initUseCase(
        authRepository: AuthRepository = AuthRepositoryFake(),
        trailRepository: TrailRepository = TrailRepositoryFake(),
    ): GetSingleTrailUseCase = GetSingleTrailUseCaseImpl(
        authRepository = authRepository,
        trailRepository = trailRepository,
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
            trailRepository = TrailRepositoryFake(error = DatabaseException.UnknownException())
        )

        // When
        useCase(id = Random.nextString())
    }

    @Test
    fun `GIVEN Trail WHEN invoke THEN return correct TrailUI`() = runTest {
        // Given
        val currentUser = Random.nextUser()
        val trail = Random.nextTrail()
        val useCase = initUseCase(
            authRepository = AuthRepositoryFake(currentUser = currentUser),
            trailRepository = TrailRepositoryFake(trails = listOf(trail)),
        )

        // When
        val trailUI = useCase(id = Random.nextString())

        // Then
        val expectedTrailUI = trail.toUIModel(currentUserId = currentUser.id)
        assertEquals(expectedTrailUI, trailUI)
    }
}