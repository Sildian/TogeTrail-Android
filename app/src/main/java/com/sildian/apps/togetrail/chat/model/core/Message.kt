package com.sildian.apps.togetrail.chat.model.core

import android.os.Parcelable
import com.sildian.apps.togetrail.common.utils.DateUtilities
import kotlinx.android.parcel.Parcelize
import java.util.*

/*************************************************************************************************
 * A message is a text sent by a user to other users
 ************************************************************************************************/

@Parcelize
data class Message (
    var id: String? = null,
    var text: String = "",
    val date: Date = Date(),
    val authorId: String = "",
    val authorName: String? = null,
    val authorPhotoUrl: String? = null
)
    : Parcelable
{

    override fun toString(): String {
        return this.text
    }

    fun writeAuthorNameAndDate(): String {
        return if (authorName != null) {
            "$authorName - ${DateUtilities.displayDateAndTimeShort(date)}"
        } else {
            ""
        }
    }
}
