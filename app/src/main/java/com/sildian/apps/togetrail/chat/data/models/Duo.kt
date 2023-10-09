package com.sildian.apps.togetrail.chat.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*************************************************************************************************
 * A Duo is a group composed by two members, allowing them to chat in a private space
 ************************************************************************************************/

@Deprecated("Replaced by new HikerConversation and ConversationUI")
@Parcelize
data class Duo (
    val interlocutorId: String = "",
    val userId: String = "",
    var interlocutorName: String? = null,
    var interlocutorPhotoUrl: String? = null,
    var lastMessage: Message? = null,
    var lastMessageReadId: String? = null,
    var nbUnreadMessages: Int = 0
)
    : Parcelable
{

    fun isLastMessageRead(): Boolean =
        lastMessageReadId == lastMessage?.id
}