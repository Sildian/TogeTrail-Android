package com.sildian.apps.togetrail.domainLayer.locationSearch

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocationSearchUseCasesModule {

    @Binds
    fun bindFindAutocompleteLocationPredictionsUseCase(
        findAutocompleteLocationPredictionsUseCase: FindAutoCompleteLocationPredictionsUseCaseImpl,
    ): FindAutocompleteLocationPredictionsUseCase

    @Binds
    fun bindFindLocationDetailsUseCase(
        findLocationDetailsUseCase: FindLocationDetailsUseCaseImpl,
    ): FindLocationDetailsUseCase
}