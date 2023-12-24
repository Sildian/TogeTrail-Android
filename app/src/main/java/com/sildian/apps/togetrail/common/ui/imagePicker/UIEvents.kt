package com.sildian.apps.togetrail.common.ui.imagePicker

sealed interface ImagePickerEvent {
    data object GetPicture : ImagePickerEvent
    data object TakePicture : ImagePickerEvent
}