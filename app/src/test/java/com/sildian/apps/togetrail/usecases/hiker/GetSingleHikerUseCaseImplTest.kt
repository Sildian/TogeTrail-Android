package com.sildian.apps.togetrail.usecases.hiker

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.nextUser
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.repositories.auth.AuthRepository
import com.sildian.apps.togetrail.repositories.auth.AuthRepositoryFake
import com.sildian.apps.togetrail.repositories.database.entities.hiker.nextHiker
import com.sildian.apps.togetrail.repositories.database.hiker.main.HikerRepository
import com.sildian.apps.togetrail.repositories.database.hiker.main.HikerRepositoryFake
import com.sildian.apps.togetrail.usecases.mappers.toUIModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GetSingleHikerUseCaseImplTest {

    private fun initUseCase(
        authRepository: AuthRepository = AuthRepositoryFake(),
        hikerRepository: HikerRepository = HikerRepositoryFake(),
    ): GetSingleHikerUseCase = GetSingleHikerUseCaseImpl(
        authRepository = authRepository,
        hikerRepository = hikerRepository,
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
            hikerRepository = HikerRepositoryFake(error = DatabaseException.UnknownException())
        )

        // When
        useCase(id = Random.nextString())
    }

    @Test
    fun `GIVEN Hiker WHEN invoke THEN return correct HikerUI`() = runTest {
        // Given
        val currentUser = Random.nextUser()
        val hiker = Random.nextHiker()
        val useCase = initUseCase(
            authRepository = AuthRepositoryFake(currentUser = currentUser),
            hikerRepository = HikerRepositoryFake(hikers = listOf(hiker)),
        )

        // When
        val hikerUI = useCase(id = Random.nextString())

        // Then
        val expectedHikerUI = hiker.toUIModel(currentUserId = currentUser.id)
        assertEquals(expectedHikerUI, hikerUI)
    }
}