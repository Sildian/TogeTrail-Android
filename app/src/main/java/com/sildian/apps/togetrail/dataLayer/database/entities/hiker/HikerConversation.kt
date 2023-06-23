package com.sildian.apps.togetrail.dataLayer.database.entities.hiker

import com.sildian.apps.togetrail.dataLayer.database.entities.conversation.Message

data class HikerConversation(
    val id: String? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val lastMessage: Message? = null,
    val nbUnreadMessages: Int? = null,
)