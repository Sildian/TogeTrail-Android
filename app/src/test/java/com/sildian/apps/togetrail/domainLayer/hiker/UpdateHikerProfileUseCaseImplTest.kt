package com.sildian.apps.togetrail.domainLayer.hiker

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.StorageException
import com.sildian.apps.togetrail.common.utils.nextUrlString
import com.sildian.apps.togetrail.dataLayer.auth.AuthRepository
import com.sildian.apps.togetrail.dataLayer.auth.AuthRepositoryFake
import com.sildian.apps.togetrail.dataLayer.database.hiker.main.HikerRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.main.HikerRepositoryFake
import com.sildian.apps.togetrail.dataLayer.storage.StorageRepository
import com.sildian.apps.togetrail.dataLayer.storage.StorageRepositoryFake
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextHikerUI
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.random.Random

class UpdateHikerProfileUseCaseImplTest {

    private fun initUseCase(
        authRepository: AuthRepository = AuthRepositoryFake(),
        storageRepository: StorageRepository = StorageRepositoryFake(),
        hikerRepository: HikerRepository = HikerRepositoryFake(),
    ): UpdateHikerUseCase =
        UpdateHikerUseCaseImpl(
            authRepository = authRepository,
            storageRepository = storageRepository,
            hikerRepository = hikerRepository,
        )

    @Test(expected = DatabaseException::class)
    fun `GIVEN DatabaseException WHEN invoke THEN throws DatabaseException`() = runTest {
        // Given
        val useCase = initUseCase(
            hikerRepository = HikerRepositoryFake(error = DatabaseException.UnknownException()),
        )

        // When
        useCase(
            hiker = Random.nextHikerUI(),
            imageUrlToDeleteFromStorage = null,
            imageUriToAddInStorage = null,
        )
    }

    @Test(expected = AuthException::class)
    fun `GIVEN AuthException WHEN invoke THEN throws AuthException`() = runTest {
        // Given
        val useCase = initUseCase(
            authRepository = AuthRepositoryFake(error = AuthException.UnknownException()),
        )

        // When
        useCase(
            hiker = Random.nextHikerUI(),
            imageUrlToDeleteFromStorage = null,
            imageUriToAddInStorage = null,
        )
    }

    @Test(expected = StorageException::class)
    fun `GIVEN StorageException with imagesUrls WHEN invoke THEN throws StorageException`() = runTest {
        // Given
        val hiker = Random.nextHikerUI()
        val imageUrlToDelete = Random.nextUrlString()
        val imageUriToAdd = Random.nextUrlString()
        val useCase = initUseCase(
            storageRepository = StorageRepositoryFake(error = StorageException.UnknownException()),
        )

        // When
        useCase(
            hiker = hiker,
            imageUrlToDeleteFromStorage = imageUrlToDelete,
            imageUriToAddInStorage = imageUriToAdd,
        )
    }

    @Test
    fun `GIVEN hiker and images WHEN invoke THEN hiker is updated and correct image is stored`() = runTest {
        // Given
        val hiker = Random.nextHikerUI()
        val imageUrlToDelete = Random.nextUrlString()
        val imageUriToAdd = Random.nextUrlString()
        val authRepository = AuthRepositoryFake()
        val storageRepository = StorageRepositoryFake()
        val hikerRepository = HikerRepositoryFake()
        val useCase = initUseCase(
            authRepository = authRepository,
            storageRepository = storageRepository,
            hikerRepository = hikerRepository,
        )

        // When
        useCase(
            hiker = hiker,
            imageUrlToDeleteFromStorage = imageUrlToDelete,
            imageUriToAddInStorage = imageUriToAdd,
        )

        // Then
        assertEquals(1, hikerRepository.addOrUpdateHikerSuccessCount)
        assertEquals(1, authRepository.updateUserSuccessCount)
        assertEquals(1, storageRepository.deleteImageSuccessCount)
        assertEquals(1, storageRepository.uploadImageSuccessCount)
    }

    @Test
    fun `GIVEN hiker without images WHEN invoke THEN hiker is updated`() = runTest {
        // Given
        val hiker = Random.nextHikerUI()
        val authRepository = AuthRepositoryFake()
        val storageRepository = StorageRepositoryFake()
        val hikerRepository = HikerRepositoryFake()
        val useCase = initUseCase(
            authRepository = authRepository,
            storageRepository = storageRepository,
            hikerRepository = hikerRepository,
        )

        // When
        useCase(
            hiker = hiker,
            imageUrlToDeleteFromStorage = null,
            imageUriToAddInStorage = null,
        )

        // Then
        assertEquals(1, hikerRepository.addOrUpdateHikerSuccessCount)
        assertEquals(1, authRepository.updateUserSuccessCount)
        assertEquals(0, storageRepository.deleteImageSuccessCount)
        assertEquals(0, storageRepository.uploadImageSuccessCount)
    }
}