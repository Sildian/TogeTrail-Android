package com.sildian.apps.togetrail.usecases.event

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface EventUseCasesModule {

    @Binds
    fun bindGetSingleEventUseCase(
        getSingleEventUseCase: GetSingleEventUseCaseImpl,
    ): GetSingleEventUseCase
}