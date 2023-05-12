package com.sildian.apps.togetrail.features.entities.conversation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class MessageUI(
    val text: String,
    val creationDate: LocalDateTime,
    val updateDate: LocalDateTime,
    val authorId: String,
    val isCurrentUserAuthor: Boolean,
) : Parcelable