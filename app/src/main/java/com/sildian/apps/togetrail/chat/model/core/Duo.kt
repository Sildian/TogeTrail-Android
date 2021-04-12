package com.sildian.apps.togetrail.chat.model.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * A Duo is a group composed by two members, allowing them to chat in a private space
 ************************************************************************************************/

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