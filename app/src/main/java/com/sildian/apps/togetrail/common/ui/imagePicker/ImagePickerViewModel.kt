package com.sildian.apps.togetrail.common.ui.imagePicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor() : ViewModel() {

    private val _event = MutableSharedFlow<ImagePickerEvent>()
    val event: SharedFlow<ImagePickerEvent> get() = _event

    fun onGetPictureButtonClick() {
        viewModelScope.launch {
            _event.emit(ImagePickerEvent.GetPicture)
        }
    }

    fun onTakePictureButtonClick() {
        viewModelScope.launch {
            _event.emit(ImagePickerEvent.TakePicture)
        }
    }
}