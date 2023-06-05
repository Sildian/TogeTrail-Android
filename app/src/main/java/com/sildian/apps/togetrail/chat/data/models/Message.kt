package com.sildian.apps.togetrail.chat.data.models

import android.os.Parcelable
import com.sildian.apps.togetrail.common.utils.DateUtilities
import kotlinx.android.parcel.Parcelize
import java.util.*

/*************************************************************************************************
 * A message is a text sent by a user to other users
 ************************************************************************************************/

@Deprecated("Replaced by new Message and MessageUI")
@Parcelize
data class Message (
    var text: String = "",
    val date: Date = Date(),
    val authorId: String = "",
    val authorName: String? = null,
    val authorPhotoUrl: String? = null
)
    : Parcelable
{
    var id: String = "" ; private set

    /*The id is composed by the authorId and the timestamp*/

    init {
        if (this.id.isEmpty()) {
            this.id = authorId + "_" + date.time
        }
    }

    override fun toString(): String {
        return this.text
    }

    fun writeAuthorNameAndDate(): String {
        return if (authorName != null) {
            "$authorName - ${DateUtilities.displayDateAndTimeRelative(date)?:""}"
        } else {
            ""
        }
    }
}
