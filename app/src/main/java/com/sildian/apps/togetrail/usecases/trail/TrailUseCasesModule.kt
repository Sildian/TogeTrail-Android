package com.sildian.apps.togetrail.usecases.trail

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TrailUseCasesModule {

    @Binds
    fun bindGetSingleTrailUseCase(
        getSingleTrailUseCase: GetSingleTrailUseCaseImpl,
    ): GetSingleTrailUseCase

    @Binds
    fun bindSearchTrailsUseCase(
        searchTrailsUseCase: SearchTrailsUseCaseImpl,
    ): SearchTrailsUseCase
}