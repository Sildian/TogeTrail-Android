package com.sildian.apps.togetrail.chat.others

import com.sildian.apps.togetrail.chat.model.core.Message

interface MessageSender {
    fun sendMessage(text: String)
    fun editMessage(message: Message, newText: String)
}