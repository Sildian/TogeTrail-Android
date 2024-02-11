package com.sildian.apps.togetrail.domainLayer.hiker

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.StorageException
import com.sildian.apps.togetrail.dataLayer.auth.AuthRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.main.HikerRepository
import com.sildian.apps.togetrail.dataLayer.storage.StorageRepository
import com.sildian.apps.togetrail.domainLayer.mappers.toDataModel
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerUI
import javax.inject.Inject
import kotlin.jvm.Throws

interface UpdateHikerProfileUseCase {

    @Throws(AuthException::class, StorageException::class, DatabaseException::class)
    suspend operator fun invoke(
        hiker: HikerUI,
        imageUrlToDeleteFromStorage: String?,
        imageUriToAddInStorage: String?,
    )
}

class UpdateHikerProfileUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val hikerRepository: HikerRepository,
) : UpdateHikerProfileUseCase {

    override suspend operator fun invoke(
        hiker: HikerUI,
        imageUrlToDeleteFromStorage: String?,
        imageUriToAddInStorage: String?
    ) {
        deleteOldImageFromStorage(imageUrl = imageUrlToDeleteFromStorage)
        val newImageUrl = uploadNewImageToStorage(imageUri = imageUriToAddInStorage)
        val updatedHiker = if (newImageUrl != null) {
            hiker.copy(photoUrl = newImageUrl)
        } else {
            hiker
        }
        hikerRepository.addOrUpdateHiker(hiker = updatedHiker.toDataModel())
        authRepository.updateUser(name = updatedHiker.name, photoUrl = updatedHiker.photoUrl)
    }

    private suspend fun deleteOldImageFromStorage(imageUrl: String?) {
        if (imageUrl != null) {
            try {
                storageRepository.deleteImage(imageUrl = imageUrl)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun uploadNewImageToStorage(imageUri: String?): String? =
        imageUri?.let {
            try {
                storageRepository.uploadImage(imageUri = imageUri)
            } catch (e: Throwable) {
                e.printStackTrace()
                null
            }
        }
}