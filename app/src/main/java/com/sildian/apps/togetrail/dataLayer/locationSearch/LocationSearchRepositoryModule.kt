package com.sildian.apps.togetrail.dataLayer.locationSearch

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocationSearchRepositoryModule {

    @Binds
    fun bindLocationSearchRepository(
        locationSearchRepository: LocationSearchRepositoryImpl
    ): LocationSearchRepository
}