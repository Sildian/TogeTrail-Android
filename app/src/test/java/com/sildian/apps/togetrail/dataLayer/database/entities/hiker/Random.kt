package com.sildian.apps.togetrail.dataLayer.database.entities.hiker

import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.utils.nextAlphaString
import com.sildian.apps.togetrail.common.utils.nextDate
import com.sildian.apps.togetrail.common.utils.nextEmailAddressString
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.common.utils.nextUrlString
import com.sildian.apps.togetrail.dataLayer.database.entities.conversation.nextMessage
import com.sildian.apps.togetrail.dataLayer.database.entities.conversation.Message
import java.util.*
import kotlin.random.Random

fun Random.nextHiker(
    id: String? = nextString(),
    email: String? = nextEmailAddressString(),
    name: String? = nextAlphaString(),
    photoUrl: String? = nextUrlString(),
    birthday: Date? = nextDate(),
    home: Location? = nextLocation(),
    description: String? = nextString(),
    profileCreationDate: Date? = nextDate(),
    nbTrailsCreated: Int? = nextInt(from = 0, until = 10),
    nbEventsCreated: Int? = nextInt(from = 0, until = 10),
    nbEventsAttended: Int? = nextInt(from = 0, until = 10),
): Hiker =
    Hiker(
        id = id,
        email = email,
        name = name,
        photoUrl = photoUrl,
        birthday = birthday,
        home = home,
        description = description,
        profileCreationDate = profileCreationDate,
        nbTrailsCreated = nbTrailsCreated,
        nbEventsCreated = nbEventsCreated,
        nbEventsAttended = nbEventsAttended,
    )

fun Random.nextHikerHistoryItem(
    date: Date? = nextDate(),
    action: HikerHistoryItem.Action? = HikerHistoryItem.Action.values().random(),
    itemId: String? = nextString(),
    itemType: HikerHistoryItem.Item.Type? = HikerHistoryItem.Item.Type.values().random(),
): HikerHistoryItem =
    HikerHistoryItem(
        date = date,
        action = action,
        item = HikerHistoryItem.Item(
            id = itemId,
            type = itemType,
        )
    )

fun Random.nextHikerEvent(
    id: String? = nextString(),
): HikerEvent =
    HikerEvent(id = id)

fun Random.nextHikerTrail(
    id: String? = nextString(),
): HikerTrail =
    HikerTrail(id = id)

fun Random.nextHikerConversation(
    id: String? = nextString(),
    name: String? = nextString(),
    photoUrl: String? = nextUrlString(),
    lastMessage: Message? = nextMessage(),
    nbUnreadMessages: Int? = nextInt(from = 0, until = 10),
): HikerConversation =
    HikerConversation(
        id = id,
        name = name,
        photoUrl = photoUrl,
        lastMessage = lastMessage,
        nbUnreadMessages = nbUnreadMessages,
    )

fun Random.nextHikersList(itemsCount: Int = nextInt(from = 1, until = 4)): List<Hiker> =
    List(size = itemsCount) { index ->
        nextHiker(id = index.toString())
    }

fun Random.nextHikerHistoryItemsList(itemsCount: Int = nextInt(from = 1, until = 4)): List<HikerHistoryItem> =
    List(size = itemsCount) {
        nextHikerHistoryItem()
    }

fun Random.nextHikerEventsList(itemsCount: Int = nextInt(from = 1, until = 4)): List<HikerEvent> =
    List(size = itemsCount) { index ->
        nextHikerEvent(id = index.toString())
    }

fun Random.nextHikerTrailsList(itemsCount: Int = nextInt(from = 1, until = 4)): List<HikerTrail> =
    List(size = itemsCount) { index ->
        nextHikerTrail(id = index.toString())
    }

fun Random.nextHikerConversationsList(itemsCount: Int = nextInt(from = 1, until = 4)): List<HikerConversation> =
    List(size = itemsCount) { index ->
        nextHikerConversation(id = index.toString())
    }