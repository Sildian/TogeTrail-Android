package com.sildian.apps.togetrail.uiLayer.hikerProfile

import android.os.Parcelable
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerUI
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed interface HikerState : Parcelable {
    @IgnoredOnParcel
    val isLoading: Boolean get() = this is Loading
    @IgnoredOnParcel
    val error: Throwable? get() = (this as? Error)?.e
    @IgnoredOnParcel
    val result: HikerUI? get() = (this as? Result)?.hiker

    @Parcelize
    object Loading : HikerState
    @Parcelize
    data class Error(val e: Throwable) : HikerState
    @Parcelize
    data class Result(val hiker: HikerUI) : HikerState
}

sealed interface HikerHistoryItemsState : Parcelable {
    @IgnoredOnParcel
    val isLoading: Boolean get() = this is Loading
    @IgnoredOnParcel
    val error: Throwable? get() = (this as? Error)?.e
    @IgnoredOnParcel
    val result: List<HikerHistoryItemUI> get() = (this as? Result)?.historyItems ?: emptyList()

    @Parcelize
    object Loading : HikerHistoryItemsState
    @Parcelize
    data class Error(val e: Throwable) : HikerHistoryItemsState
    @Parcelize
    data class Result(val historyItems: List<HikerHistoryItemUI>) : HikerHistoryItemsState
}
