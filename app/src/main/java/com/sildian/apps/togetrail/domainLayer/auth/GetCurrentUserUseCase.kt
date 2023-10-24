package com.sildian.apps.togetrail.domainLayer.auth

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.User
import com.sildian.apps.togetrail.dataLayer.auth.AuthRepository
import javax.inject.Inject
import kotlin.jvm.Throws

interface GetCurrentUserUseCase {
    @Throws(AuthException::class, IllegalStateException::class)
    operator fun invoke(): User?
}

class GetCurrentUserUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : GetCurrentUserUseCase {

    override operator fun invoke(): User? = authRepository.getCurrentUser()
}