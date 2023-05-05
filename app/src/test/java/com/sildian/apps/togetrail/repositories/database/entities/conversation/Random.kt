package com.sildian.apps.togetrail.repositories.database.entities.conversation

import com.sildian.apps.togetrail.common.core.nextDate
import com.sildian.apps.togetrail.common.core.nextString
import java.util.*
import kotlin.random.Random

fun Random.nextConversation(
    id: String? = nextString(),
): Conversation =
    Conversation(id = id)

fun Random.nextMessage(
    text: String? = nextString(),
    creationDate: Date? = nextDate(),
    updateDate: Date? = nextDate(),
    authorId: String? = nextString(),
): Message =
    Message(
        text = text,
        creationDate = creationDate,
        updateDate = updateDate,
        authorId = authorId,
    )

fun Random.nextMessagesList(itemsCount: Int = nextInt(from = 0, until = 4)): List<Message> =
    List(size = itemsCount) {
        nextMessage()
    }