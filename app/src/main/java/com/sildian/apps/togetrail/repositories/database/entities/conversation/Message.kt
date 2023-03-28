package com.sildian.apps.togetrail.repositories.database.entities.conversation

import java.util.*

data class Message(
    val text: String? = null,
    val creationDate: Date? = null,
    val updateDate: Date? = null,
    val author: Author? = null,
) {

    val id: String?
        get() {
            val authorId = author?.id ?: return null
            val timeStamp = creationDate?.time ?: return null
            return authorId + "_" + timeStamp
        }

    data class Author(
        val id: String? = null,
        val name: String? = null,
        val photoUrl: String? = null,
    )
}
