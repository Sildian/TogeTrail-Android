package com.sildian.apps.togetrail.usecases.hiker

import com.sildian.apps.togetrail.features.entities.hiker.HikerUI
import com.sildian.apps.togetrail.features.entities.hiker.nextHikerUI
import kotlin.random.Random

class GetSingleHikerUseCaseFake(
    private val error: Throwable? = null,
    private val hiker: HikerUI = Random.nextHikerUI(),
) : GetSingleHikerUseCase {

    override suspend operator fun invoke(id: String): HikerUI =
        error?.let { throw it } ?: hiker
}