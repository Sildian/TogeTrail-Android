package com.sildian.apps.togetrail.message.model.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/*************************************************************************************************
 * A message is a text sent by a user to other users
 ************************************************************************************************/

@Parcelize
data class Message (
    val text: String = "",
    val date: Date = Date(),
    val authorId: String = "",
    val authorName: String? = null,
    val authorPhotoUrl: String? = null
)
    : Parcelable
