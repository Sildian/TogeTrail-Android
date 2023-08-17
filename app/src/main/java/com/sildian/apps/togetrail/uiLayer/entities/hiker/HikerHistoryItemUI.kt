package com.sildian.apps.togetrail.uiLayer.entities.hiker

import android.os.Parcelable
import com.sildian.apps.togetrail.common.core.location.Location
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

sealed interface HikerHistoryItemUI : Parcelable {
    val date: LocalDateTime
    val hikerInfo: HikerInfo
    val itemInfo: ItemInfo

    @Parcelize
    data class HikerInfo(
        val id: String,
        val name: String,
        val photoUrl: String?,
    ) : Parcelable

    @Parcelize
    data class ItemInfo(
        val id: String,
        val name: String,
        val photoUrl: String?,
        val location: Location?,
    ) : Parcelable

    @Parcelize
    data class HikerRegistered(
        override val date: LocalDateTime,
        override val hikerInfo: HikerInfo,
    ) : HikerHistoryItemUI {
        override val itemInfo: ItemInfo
            get() = ItemInfo(
                id = hikerInfo.id,
                name = hikerInfo.name,
                photoUrl = hikerInfo.photoUrl,
                location = null,
            )
    }

    @Parcelize
    data class TrailCreated(
        override val date: LocalDateTime,
        override val hikerInfo: HikerInfo,
        override val itemInfo: ItemInfo,
    ) : HikerHistoryItemUI

    @Parcelize
    data class EventCreated(
        override val date: LocalDateTime,
        override val hikerInfo: HikerInfo,
        override val itemInfo: ItemInfo,
    ) : HikerHistoryItemUI

    @Parcelize
    data class EventAttended(
        override val date: LocalDateTime,
        override val hikerInfo: HikerInfo,
        override val itemInfo: ItemInfo,
    ) : HikerHistoryItemUI
}