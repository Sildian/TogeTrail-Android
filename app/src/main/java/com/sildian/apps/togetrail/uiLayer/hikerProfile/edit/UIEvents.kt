package com.sildian.apps.togetrail.uiLayer.hikerProfile.edit

import java.time.LocalDate

sealed interface HikerProfileEditEvent {
    data object ShowImagePicker : HikerProfileEditEvent
    data class ShowDatePicker(val selectedDate: LocalDate?) : HikerProfileEditEvent
    data object ShowLocationPicker : HikerProfileEditEvent
    data object NotifyProfileUpdateEmpty : HikerProfileEditEvent
    data object NotifyProfileUpdateFieldsError : HikerProfileEditEvent
    data class NotifyProfileUpdateError(val e: Throwable) : HikerProfileEditEvent
    data object NotifyProfileUpdateSuccess : HikerProfileEditEvent
}