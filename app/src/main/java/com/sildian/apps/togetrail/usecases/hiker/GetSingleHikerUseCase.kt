package com.sildian.apps.togetrail.usecases.hiker

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.features.entities.hiker.HikerUI
import com.sildian.apps.togetrail.repositories.auth.AuthRepository
import com.sildian.apps.togetrail.repositories.database.hiker.main.HikerRepository
import com.sildian.apps.togetrail.usecases.mappers.toUIModel
import javax.inject.Inject
import kotlin.jvm.Throws

interface GetSingleHikerUseCase {
    @Throws(AuthException::class, DatabaseException::class, IllegalStateException::class)
    suspend operator fun invoke(id: String): HikerUI
}

class GetSingleHikerUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val hikerRepository: HikerRepository,
) : GetSingleHikerUseCase {

    override suspend operator fun invoke(id: String): HikerUI =
        hikerRepository
            .getHiker(id = id)
            .toUIModel(currentUserId = authRepository.getCurrentUser()?.id)
}