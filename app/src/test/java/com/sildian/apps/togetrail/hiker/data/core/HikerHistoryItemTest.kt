package com.sildian.apps.togetrail.hiker.data.core

import android.content.Context
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HikerHistoryItemTest {

    private lateinit var context: Context

    @Before
    @Suppress("DEPRECATION")
    fun setup() {
        this.context = RuntimeEnvironment.application.applicationContext
    }

    @Test
    fun given_hikerRegistered_when_writeItemHistory_then_checkResult() {
        val historyItem=HikerHistoryItem(type=HikerHistoryType.HIKER_REGISTERED)
        val display=historyItem.writeItemHistory(context, "Toto")
        val expectedResult=when(this.context.resources.configuration.locale){
            Locale.FRANCE -> "Toto a créé son profil TogeTrail"
            else -> "Toto registered on TogeTrail"
        }
        assertEquals(expectedResult, display)
    }

    @Test
    fun given_trailCreated_when_writeItemHistory_then_checkResult() {
        val historyItem=HikerHistoryItem(
            type=HikerHistoryType.TRAIL_CREATED,
            itemName="Hell Trail",
            locationName = "Alberta, Canada"
        )
        val display=historyItem.writeItemHistory(context, "Toto")
        val expectedResult=when(this.context.resources.configuration.locale){
            Locale.FRANCE -> "Toto a créé une nouvelle rando en Alberta, Canada"
            else -> "Toto created a new trail in Alberta, Canada"
        }
        assertEquals(expectedResult, display)
    }

    @Test
    fun given_eventCreated_when_writeItemHistory_then_checkResult() {
        val historyItem=HikerHistoryItem(
            type=HikerHistoryType.EVENT_CREATED,
            itemName="Hell Event",
            locationName = "Alberta, Canada"
        )
        val display=historyItem.writeItemHistory(context, "Toto")
        val expectedResult=when(this.context.resources.configuration.locale){
            Locale.FRANCE -> "Toto a créé un nouvel évènement en Alberta, Canada"
            else -> "Toto created a new event in Alberta, Canada"
        }
        assertEquals(expectedResult, display)
    }

    @Test
    fun given_eventRegistered_when_writeItemHistory_then_checkResult() {
        val historyItem=HikerHistoryItem(
            type=HikerHistoryType.EVENT_ATTENDED,
            itemName="Hell Event",
            locationName = "Alberta, Canada"
        )
        val display=historyItem.writeItemHistory(context, "Toto")
        val expectedResult=when(this.context.resources.configuration.locale){
            Locale.FRANCE -> "Toto a décidé de participer à un évènement en Alberta, Canada"
            else -> "Toto decided to attend an event in Alberta, Canada"
        }
        assertEquals(expectedResult, display)
    }
}