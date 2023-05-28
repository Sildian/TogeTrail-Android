package com.sildian.apps.togetrail.features.entities.conversation

import com.sildian.apps.togetrail.common.core.nextLocalDateTime
import com.sildian.apps.togetrail.common.core.nextString
import java.time.LocalDateTime
import kotlin.random.Random

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