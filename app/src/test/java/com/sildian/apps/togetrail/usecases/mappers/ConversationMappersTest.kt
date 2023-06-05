package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.features.entities.conversation.nextConversationUI
import com.sildian.apps.togetrail.repositories.database.entities.hiker.nextHikerConversation
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class ConversationMappersTest {

    @Test
    fun `GIVEN ConversationUI WHEN invoking toDataModel THEN result is correct HikerConversation`() {
        // Given
        val conversationUI = Random.nextConversationUI()

        // When
        val hikerConversation = conversationUI.toDataModel()

        // Then
        assertEquals(conversationUI.id, hikerConversation.id)
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerConversation without id WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hikerConversation = Random.nextHikerConversation(id = null)

        // When
        hikerConversation.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerConversation without name WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hikerConversation = Random.nextHikerConversation(name = null)

        // When
        hikerConversation.toUIModel(currentUserId = Random.nextString())
    }

    @Test
    fun `GIVEN valid HikerConversation WHEN invoking toUIModel THEN result is correct ConversationUI`() {
        // Given
        val hikerConversation = Random.nextHikerConversation()

        // When
        val conversationUI = hikerConversation.toUIModel(currentUserId = Random.nextString())

        // Then
        assertEquals(hikerConversation.id, conversationUI.id)
    }
}