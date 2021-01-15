package com.sildian.apps.togetrail.chat.model.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * A chatRoom is a private virtual place where users can chat
 ************************************************************************************************/

@Parcelize
data class ChatRoom (
    var name: String? = null,
    var photoUrl: String? = null,
    val membersIds: List<String> = listOf(),
    var lastMessage: Message? = null,
    val lastReadMessageByMember: HashMap<String, String?> = hashMapOf()     //Key = memberId, Entry = messageId
)
    : Parcelable
{
    var id: String = "" ; private set

    /**The id is composed by the two members ids**/

    init {
        if (this.id.isEmpty()) {
            if (this.membersIds.size == 2) {
                this.id = this.membersIds.first() + "_" + this.membersIds.last()
            }
        }
    }
}