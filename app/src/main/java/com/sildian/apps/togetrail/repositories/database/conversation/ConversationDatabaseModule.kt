package com.sildian.apps.togetrail.repositories.database.conversation

import com.sildian.apps.togetrail.repositories.database.conversation.main.ConversationRepository
import com.sildian.apps.togetrail.repositories.database.conversation.main.ConversationRepositoryImpl
import com.sildian.apps.togetrail.repositories.database.conversation.message.ConversationMessageRepository
import com.sildian.apps.togetrail.repositories.database.conversation.message.ConversationMessageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ConversationDatabaseModule {

    @Binds
    fun bindConversationRepository(
        conversationRepository: ConversationRepositoryImpl
    ): ConversationRepository

    @Binds
    fun bindConversationMessageRepository(
        conversationMessageRepository: ConversationMessageRepositoryImpl
    ): ConversationMessageRepository
}