package com.sildian.apps.togetrail.uiLayer.hikerProfile.edit

import com.sildian.apps.togetrail.common.core.location.Location
import java.time.LocalDate

sealed interface HikerProfileEditAction {
    data class UpdateTextField(
        val field: HikerProfileEditTextField,
        val value: String,
    ) : HikerProfileEditAction
    data class UpdatePhoto(val uri: String) : HikerProfileEditAction
    data class UpdateBirthday(val date: LocalDate) : HikerProfileEditAction
    data class UpdateHome(val location: Location) : HikerProfileEditAction
}