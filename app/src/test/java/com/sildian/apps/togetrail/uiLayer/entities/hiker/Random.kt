package com.sildian.apps.togetrail.uiLayer.entities.hiker

import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.utils.nextEmailAddressString
import com.sildian.apps.togetrail.common.utils.nextLocalDate
import com.sildian.apps.togetrail.common.utils.nextLocalDateTime
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.common.utils.nextUrlString
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

fun Random.nextHikerUI(
    id: String = nextString(),
    email: String = nextEmailAddressString(),
    name: String = nextString(),
    photoUrl: String? = nextUrlString(),
    birthday: LocalDate? = nextLocalDate(),
    home: Location? = nextLocation(),
    description: String = nextString(),
    profileCreationDate: LocalDateTime = nextLocalDateTime(),
    isCurrentUser: Boolean = nextBoolean(),
    nbTrailsCreated: Int = nextInt(from = 0, until = 10),
    nbEventsCreated: Int = nextInt(from = 0, until = 10),
    nbEventsAttended: Int = nextInt(from = 0, until = 10),
): HikerUI =
    HikerUI(
        id = id,
        email = email,
        name = name,
        photoUrl = photoUrl,
        birthday = birthday,
        home = home,
        description = description,
        profileCreationDate = profileCreationDate,
        isCurrentUser = isCurrentUser,
        nbTrailsCreated = nbTrailsCreated,
        nbEventsCreated = nbEventsCreated,
        nbEventsAttended = nbEventsAttended,
    )

fun Random.nextHikerHistoryItemUI(
    date: LocalDateTime = nextLocalDateTime(),
    action: HikerHistoryItemUI.Action = HikerHistoryItemUI.Action.values().random(),
    itemId: String = nextString(),
    itemType: HikerHistoryItemUI.Item.Type = HikerHistoryItemUI.Item.Type.values().random(),
): HikerHistoryItemUI =
    HikerHistoryItemUI(
        date = date,
        action = action,
        item = HikerHistoryItemUI.Item(
            id = itemId,
            type = itemType,
        )
    )

fun Random.nextHikersUIList(itemsCount: Int = nextInt(from = 1, until = 4)): List<HikerUI> =
    List(size = itemsCount) { index ->
        nextHikerUI(id = index.toString())
    }

fun Random.nextHikerHistoryItemsUIList(itemsCount: Int = nextInt(from = 1, until = 4)): List<HikerHistoryItemUI> =
    List(size = itemsCount) {
        nextHikerHistoryItemUI()
    }