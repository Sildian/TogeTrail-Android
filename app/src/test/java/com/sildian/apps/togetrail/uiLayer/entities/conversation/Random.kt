package com.sildian.apps.togetrail.uiLayer.entities.conversation

import com.sildian.apps.togetrail.common.utils.nextLocalDateTime
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.common.utils.nextUrlString
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

fun Random.nextMessagesUIList(itemsCount: Int = nextInt(from = 1, until = 4)): List<MessageUI> =
    List(size = itemsCount) {
        nextMessageUI()
    }