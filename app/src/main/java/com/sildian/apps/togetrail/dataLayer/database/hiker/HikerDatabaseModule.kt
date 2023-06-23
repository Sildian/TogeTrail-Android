package com.sildian.apps.togetrail.dataLayer.database.hiker

import com.sildian.apps.togetrail.dataLayer.database.hiker.attendedEvent.HikerAttendedEventRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.attendedEvent.HikerAttendedEventRepositoryImpl
import com.sildian.apps.togetrail.dataLayer.database.hiker.conversation.HikerConversationRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.conversation.HikerConversationRepositoryImpl
import com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem.HikerHistoryItemRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem.HikerHistoryItemRepositoryImpl
import com.sildian.apps.togetrail.dataLayer.database.hiker.likedTrail.HikerLikedTrailRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.likedTrail.HikerLikedTrailRepositoryImpl
import com.sildian.apps.togetrail.dataLayer.database.hiker.main.HikerRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.main.HikerRepositoryImpl
import com.sildian.apps.togetrail.dataLayer.database.hiker.markedTrail.HikerMarkedTrailRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.markedTrail.HikerMarkedTrailRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface HikerDatabaseModule {

    @Binds
    fun bindHikerRepository(hikerRepository: HikerRepositoryImpl): HikerRepository

    @Binds
    fun bindHikerHistoryItemRepository(
        hikerHistoryItemRepository: HikerHistoryItemRepositoryImpl
    ): HikerHistoryItemRepository

    @Binds
    fun bindHikerAttendedEventRepository(
        hikerAttendedEventRepository: HikerAttendedEventRepositoryImpl
    ): HikerAttendedEventRepository

    @Binds
    fun bindHikerLikedTrailRepository(
        hikerLikedTrailRepository: HikerLikedTrailRepositoryImpl
    ): HikerLikedTrailRepository

    @Binds
    fun bindHikerMarkedTrailRepository(
        hikerMarkedTrailRepository: HikerMarkedTrailRepositoryImpl
    ): HikerMarkedTrailRepository

    @Binds
    fun bindHikerConversationRepository(
        hikerConversationRepository: HikerConversationRepositoryImpl
    ): HikerConversationRepository
}