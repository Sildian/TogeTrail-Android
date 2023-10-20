package com.sildian.apps.togetrail.common.coroutines

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoroutineMainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoroutineIODispatcher

@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatchersModule {

    @CoroutineMainDispatcher
    @Provides
    fun provideCoroutineMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @CoroutineIODispatcher
    @Provides
    fun provideCoroutineIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}