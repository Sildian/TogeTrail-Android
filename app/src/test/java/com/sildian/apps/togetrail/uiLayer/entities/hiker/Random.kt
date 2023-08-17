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

fun Random.nextHikerHistoryItemUI(): HikerHistoryItemUI =
    when (HikerHistoryItemUI::class.sealedSubclasses.random()) {
        HikerHistoryItemUI.HikerRegistered::class -> nextHikerRegisteredHistoryItemUI()
        HikerHistoryItemUI.TrailCreated::class -> nextTrailCreatedHistoryItemUI()
        HikerHistoryItemUI.EventCreated::class -> nextEventCreatedHistoryItemUI()
        HikerHistoryItemUI.EventAttended::class -> nextEventAttendedHistoryItemUI()
        else -> nextHikerRegisteredHistoryItemUI()
    }

fun Random.nextHikerRegisteredHistoryItemUI(
    date: LocalDateTime = nextLocalDateTime(),
    hikerInfo: HikerHistoryItemUI.HikerInfo = nextHikerHistoryItemUIHikerInfo(),
): HikerHistoryItemUI =
    HikerHistoryItemUI.HikerRegistered(
        date = date,
        hikerInfo = hikerInfo,
    )

fun Random.nextTrailCreatedHistoryItemUI(
    date: LocalDateTime = nextLocalDateTime(),
    hikerInfo: HikerHistoryItemUI.HikerInfo = nextHikerHistoryItemUIHikerInfo(),
    itemInfo: HikerHistoryItemUI.ItemInfo = nextHikerHistoryItemUIItemInfo(),
): HikerHistoryItemUI =
    HikerHistoryItemUI.TrailCreated(
        date = date,
        hikerInfo = hikerInfo,
        itemInfo = itemInfo,
    )

fun Random.nextEventCreatedHistoryItemUI(
    date: LocalDateTime = nextLocalDateTime(),
    hikerInfo: HikerHistoryItemUI.HikerInfo = nextHikerHistoryItemUIHikerInfo(),
    itemInfo: HikerHistoryItemUI.ItemInfo = nextHikerHistoryItemUIItemInfo(),
): HikerHistoryItemUI =
    HikerHistoryItemUI.EventCreated(
        date = date,
        hikerInfo = hikerInfo,
        itemInfo = itemInfo,
    )

fun Random.nextEventAttendedHistoryItemUI(
    date: LocalDateTime = nextLocalDateTime(),
    hikerInfo: HikerHistoryItemUI.HikerInfo = nextHikerHistoryItemUIHikerInfo(),
    itemInfo: HikerHistoryItemUI.ItemInfo = nextHikerHistoryItemUIItemInfo(),
): HikerHistoryItemUI =
    HikerHistoryItemUI.EventAttended(
        date = date,
        hikerInfo = hikerInfo,
        itemInfo = itemInfo,
    )

fun Random.nextHikerHistoryItemUIHikerInfo(
    id: String = nextString(),
    name: String = nextString(),
    photoUrl: String? = nextUrlString(),
): HikerHistoryItemUI.HikerInfo =
    HikerHistoryItemUI.HikerInfo(
        id = id,
        name = name,
        photoUrl = photoUrl,
    )

fun Random.nextHikerHistoryItemUIItemInfo(
    id: String = nextString(),
    name: String = nextString(),
    photoUrl: String? = nextUrlString(),
    location: Location? = nextLocation(),
): HikerHistoryItemUI.ItemInfo =
    HikerHistoryItemUI.ItemInfo(
        id = id,
        name = name,
        photoUrl = photoUrl,
        location = location,
    )

fun Random.nextHikersUIList(itemsCount: Int = nextInt(from = 1, until = 4)): List<HikerUI> =
    List(size = itemsCount) { index ->
        nextHikerUI(id = index.toString())
    }

fun Random.nextHikerHistoryItemsUIList(itemsCount: Int = nextInt(from = 1, until = 4)): List<HikerHistoryItemUI> =
    List(size = itemsCount) {
        nextHikerHistoryItemUI()
    }