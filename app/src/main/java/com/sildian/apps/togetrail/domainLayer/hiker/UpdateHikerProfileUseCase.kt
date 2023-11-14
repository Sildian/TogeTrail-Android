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
        if (imageUrlToDeleteFromStorage != null) {
            storageRepository.deleteImage(imageUrl = imageUrlToDeleteFromStorage)
        }
        val newImageUrl = if (imageUriToAddInStorage != null) {
            storageRepository.uploadImage(imageLocalPath = imageUriToAddInStorage)
        } else {
            null
        }?.toString()
        val updatedHiker = if (newImageUrl != null) {
            hiker.copy(photoUrl = newImageUrl)
        } else {
            hiker
        }
        hikerRepository.addOrUpdateHiker(hiker = updatedHiker.toDataModel())
        authRepository.updateUser(name = hiker.name, photoUrl = newImageUrl)
    }
}