package com.sildian.apps.togetrail.repositories.database.entities.hiker

import com.sildian.apps.togetrail.repositories.database.entities.conversation.Message

data class HikerConversation(
    val id: String? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val lastMessage: Message? = null,
    val nbUnreadMessages: Int = 0,
)