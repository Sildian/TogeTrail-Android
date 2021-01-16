package com.sildian.apps.togetrail.chat.model.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * A DuoChatRoom is a private virtual place where two users can chat
 * The current user chats with the interlocutor
 ************************************************************************************************/

@Parcelize
data class DuoChatRoom (
    val interlocutorId: String = "",
    var lastMessage: Message? = null,
    var lastMessageReadId: String? = null
)
    : Parcelable