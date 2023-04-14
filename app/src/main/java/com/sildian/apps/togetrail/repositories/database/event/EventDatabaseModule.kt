package com.sildian.apps.togetrail.repositories.database.event

import com.sildian.apps.togetrail.repositories.database.event.attachedTrail.EventAttachedTrailRepository
import com.sildian.apps.togetrail.repositories.database.event.attachedTrail.EventAttachedTrailRepositoryImpl
import com.sildian.apps.togetrail.repositories.database.event.main.EventRepository
import com.sildian.apps.togetrail.repositories.database.event.main.EventRepositoryImpl
import com.sildian.apps.togetrail.repositories.database.event.message.EventMessageRepository
import com.sildian.apps.togetrail.repositories.database.event.message.EventMessageRepositoryImpl
import com.sildian.apps.togetrail.repositories.database.event.registeredHiker.EventRegisteredHikerRepository
import com.sildian.apps.togetrail.repositories.database.event.registeredHiker.EventRegisteredHikerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface EventDatabaseModule {

    @Binds
    fun bindEventRepository(eventRepository: EventRepositoryImpl): EventRepository

    @Binds
    fun bindEventAttachedTrailRepository(
        eventAttachedTrailRepository: EventAttachedTrailRepositoryImpl
    ): EventAttachedTrailRepository

    @Binds
    fun bindEventRegisteredHikerRepository(
        eventRegisteredHikerRepository: EventRegisteredHikerRepositoryImpl
    ): EventRegisteredHikerRepository

    @Binds
    fun bindEventMessageRepository(
        eventMessageRepository: EventMessageRepositoryImpl
    ): EventMessageRepository
}