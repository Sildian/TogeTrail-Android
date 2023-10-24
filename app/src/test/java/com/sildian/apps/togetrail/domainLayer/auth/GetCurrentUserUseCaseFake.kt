package com.sildian.apps.togetrail.domainLayer.auth

import com.sildian.apps.togetrail.common.network.User
import com.sildian.apps.togetrail.common.network.nextUser
import kotlin.random.Random

class GetCurrentUserUseCaseFake(
    private val error: Throwable? = null,
    private val user: User? = Random.nextUser(),
) : GetCurrentUserUseCase {

    override operator fun invoke(): User? =
        error?.let { throw it } ?: user
}