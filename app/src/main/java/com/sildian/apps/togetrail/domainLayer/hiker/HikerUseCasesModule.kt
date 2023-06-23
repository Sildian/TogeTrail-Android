package com.sildian.apps.togetrail.domainLayer.hiker

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface HikerUseCasesModule {

    @Binds
    fun bindGetSingleHikerUseCase(
        getSingleHikerUseCase: GetSingleHikerUseCaseImpl,
    ): GetSingleHikerUseCase
}