package com.sildian.apps.togetrail.uiLayer.hikerProfile.edit

import android.os.Parcelable
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerUI
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed interface HikerEditState : Parcelable {
    @IgnoredOnParcel
    val isInitializing: Boolean get() = this is Initializing
    @IgnoredOnParcel
    val errors: List<HikerProfileEditTextField>? get() = (this as? Initialized.Edited)?.errorFields
    @IgnoredOnParcel
    val data: HikerUI? get() = (this as? Initialized)?.hiker

    @Parcelize
    data object Initializing : HikerEditState
    sealed interface Initialized : HikerEditState {
        val hiker: HikerUI
        val oldPhotoUrl: String?
        val newPhotoUri: String?
        val errorFields: List<HikerProfileEditTextField>
        @Parcelize
        data class NotEdited(override val hiker: HikerUI) : Initialized {
            @IgnoredOnParcel
            override val oldPhotoUrl: String? = null
            @IgnoredOnParcel
            override val newPhotoUri: String? = null
            @IgnoredOnParcel
            override val errorFields: List<HikerProfileEditTextField> = emptyList()
        }
        @Parcelize
        data class Edited(
            override val hiker: HikerUI,
            override val oldPhotoUrl: String?,
            override val newPhotoUri: String?,
            override val errorFields: List<HikerProfileEditTextField>,
        ) : Initialized
    }
}
