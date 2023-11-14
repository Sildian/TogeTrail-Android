package com.sildian.apps.togetrail.domainLayer.hiker

import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerUI

class UpdateHikerProfileUseCaseFake(
    private val error: Throwable? = null,
) : UpdateHikerProfileUseCase {

    var successCount: Int = 0 ; private set

    override suspend operator fun invoke(
        hiker: HikerUI,
        imageUrlToDeleteFromStorage: String?,
        imageUriToAddInStorage: String?,
    ) {
        error?.let { throw it } ?: successCount++
    }
}