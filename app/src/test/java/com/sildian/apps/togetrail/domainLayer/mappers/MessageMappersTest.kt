package com.sildian.apps.togetrail.domainLayer.mappers

import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.uiLayer.entities.conversation.nextMessageUI
import com.sildian.apps.togetrail.dataLayer.database.entities.conversation.nextMessage
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class MessageMappersTest {

    @Test
    fun `GIVEN MessageUI WHEN invoking toDataModel THEN result is correct Message`() {
        // Given
        val messageUI = Random.nextMessageUI()

        // When
        val message = messageUI.toDataModel()

        // Then
        assertEquals(messageUI.text, message.text)
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Message without text WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val message = Random.nextMessage(text = null)

        // When
        message.toUIModel(currentUserId = Random.nextString())
    }

    @Test
    fun `GIVEN valid Message WHEN invoking toUIModel THEN result is correct MessageUI`() {
        // Given
        val message = Random.nextMessage()

        // When
        val messageUI = message.toUIModel(currentUserId = Random.nextString())

        // Then
        assertEquals(message.text, messageUI.text)
    }

    @Test
    fun `GIVEN valid Message and not logged user WHEN invoking toUIModel THEN result is correct MessageUI with false isCurrentUserAuthor`() {
        // Given
        val message = Random.nextMessage()

        // When
        val messageUI = message.toUIModel(currentUserId = null)

        // Then
        assertFalse(messageUI.isCurrentUserAuthor)
    }

    @Test
    fun `GIVEN valid Message and random user WHEN invoking toUIModel THEN result is correct MessageUI with false isCurrentUserAuthor`() {
        // Given
        val message = Random.nextMessage()

        // When
        val messageUI = message.toUIModel(currentUserId = Random.nextString().replace(message.authorId.toString(), ""))

        // Then
        assertFalse(messageUI.isCurrentUserAuthor)
    }

    @Test
    fun `GIVEN valid Message and current user WHEN invoking toUIModel THEN result is correct MessageUI with true isCurrentUserAuthor`() {
        // Given
        val message = Random.nextMessage()

        // When
        val messageUI = message.toUIModel(currentUserId = message.authorId)

        // Then
        assertTrue(messageUI.isCurrentUserAuthor)
    }
}