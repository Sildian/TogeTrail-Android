package com.sildian.apps.togetrail.features.entities.conversation

import android.os.Parcelable
import com.sildian.apps.togetrail.repositories.database.entities.conversation.Message
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConversationUI(
    val id: String,
    val name: String,
    val photoUrl: String?,
    val lastMessage: Message?,
    val nbUnreadMessages: Int,
) : Parcelable