package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.features.entities.conversation.ConversationUI
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerConversation
import kotlin.jvm.Throws

fun ConversationUI.toDataModel(): HikerConversation =
    HikerConversation(
        id = id,
        name = name,
        photoUrl = photoUrl,
        lastMessage = lastMessage?.toDataModel(),
        nbUnreadMessages = nbUnreadMessages,
    )

@Throws(IllegalStateException::class)
fun HikerConversation.toUIModel(currentUserId: String?): ConversationUI {
    val id = id ?: throw IllegalStateException("Conversation should provide id")
    val name = name ?: throw IllegalStateException("Conversation should provide name")
    val photoUrl = photoUrl
    val lastMessage = lastMessage?.toUIModel(currentUserId = currentUserId)
    val nbUnreadMessages = nbUnreadMessages ?: 0
    return ConversationUI(
        id = id,
        name = name,
        photoUrl = photoUrl,
        lastMessage = lastMessage,
        nbUnreadMessages = nbUnreadMessages,
    )
}