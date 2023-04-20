package com.sildian.apps.togetrail.repositories.database.entities.conversation

import java.util.*

data class Message(
    val text: String? = null,
    val creationDate: Date? = null,
    val updateDate: Date? = null,
    val authorId: String? = null,
) {

    val id: String?
        get() {
            val authorId = authorId ?: return null
            val timeStamp = creationDate?.time ?: return null
            return authorId + "_" + timeStamp
        }
}
