package com.sildian.apps.togetrail.features.entities.conversation

import com.sildian.apps.togetrail.common.core.nextLocalDateTime
import com.sildian.apps.togetrail.common.core.nextString
import com.sildian.apps.togetrail.common.core.nextUrlString
import java.time.LocalDateTime
import kotlin.random.Random

fun Random.nextConversationUI(
    id: String = nextString(),
    name: String = nextString(),
    photoUrl: String? = nextUrlString(),
    lastMessage: MessageUI? = nextMessageUI(),
    nbUnreadMessages: Int = nextInt(from = 0, until = 10),
): ConversationUI =
    ConversationUI(
        id = id,
        name = name,
        photoUrl = photoUrl,
        lastMessage = lastMessage,
        nbUnreadMessages = nbUnreadMessages,
    )

fun Random.nextMessageUI(
    text: String = nextString(),
    creationDate: LocalDateTime = nextLocalDateTime(),
    updateDate: LocalDateTime = nextLocalDateTime(),
    authorId: String = nextString(),
    isCurrentUserAuthor: Boolean = nextBoolean(),
): MessageUI =
    MessageUI(
        text = text,
        creationDate = creationDate,
        updateDate = updateDate,
        authorId = authorId,
        isCurrentUserAuthor = isCurrentUserAuthor,
    )