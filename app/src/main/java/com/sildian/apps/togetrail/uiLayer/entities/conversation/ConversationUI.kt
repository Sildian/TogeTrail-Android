package com.sildian.apps.togetrail.uiLayer.entities.conversation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConversationUI(
    val id: String,
    val name: String,
    val photoUrl: String?,
    val lastMessage: MessageUI?,
    val nbUnreadMessages: Int,
) : Parcelable