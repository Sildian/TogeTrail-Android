package com.sildian.apps.togetrail.uiLayer.entities.hiker

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class HikerHistoryItemUI(
    val date: LocalDateTime,
    val action: Action,
    val item: Item,
) : Parcelable {

    @Parcelize
    enum class Action : Parcelable {
        HIKER_REGISTERED,
        TRAIL_CREATED,
        EVENT_CREATED,
        EVENT_ATTENDED,
    }

    @Parcelize
    data class Item(
        val id: String,
        val type: Type,
    ) : Parcelable {

        @Parcelize
        enum class Type : Parcelable {
            HIKER,
            TRAIL,
            EVENT,
        }
    }
}