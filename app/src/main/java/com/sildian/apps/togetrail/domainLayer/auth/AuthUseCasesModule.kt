package com.sildian.apps.togetrail.domainLayer.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AuthUseCasesModule {

    @Binds
    fun bindGetCurrentUserUseCase(
        getCurrentUserUseCase: GetCurrentUserUseCaseImpl,
    ): GetCurrentUserUseCase
}