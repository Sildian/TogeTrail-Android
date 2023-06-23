package com.sildian.apps.togetrail.domainLayer.mappers

import com.sildian.apps.togetrail.common.utils.toDate
import com.sildian.apps.togetrail.common.utils.toLocalDateTime
import com.sildian.apps.togetrail.uiLayer.entities.conversation.MessageUI
import com.sildian.apps.togetrail.dataLayer.database.entities.conversation.Message
import java.time.LocalDateTime

fun MessageUI.toDataModel(): Message =
    Message(
        text = text,
        creationDate = creationDate.toDate(),
        updateDate = updateDate.toDate(),
        authorId = authorId,
    )

@Throws(IllegalStateException::class)
fun Message.toUIModel(currentUserId: String?): MessageUI {
    val text = text ?: throw IllegalStateException("Message text should be provided")
    val creationDate = creationDate?.toLocalDateTime() ?: LocalDateTime.now()
    val updateDate = updateDate?.toLocalDateTime() ?: LocalDateTime.now()
    val authorId = authorId.orEmpty()
    val isCurrentUserAuthor = currentUserId.isNullOrBlank().not() && currentUserId == authorId
    return MessageUI(
        text = text,
        creationDate = creationDate,
        updateDate = updateDate,
        authorId = authorId,
        isCurrentUserAuthor = isCurrentUserAuthor,
    )
}