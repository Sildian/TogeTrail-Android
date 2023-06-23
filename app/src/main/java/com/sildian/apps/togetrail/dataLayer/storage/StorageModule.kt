package com.sildian.apps.togetrail.dataLayer.storage

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface StorageModule {

    @Binds
    fun bindStorageRepository(storageRepository: StorageRepositoryImpl): StorageRepository
}