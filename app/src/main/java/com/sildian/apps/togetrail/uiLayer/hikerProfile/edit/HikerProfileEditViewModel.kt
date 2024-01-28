package com.sildian.apps.togetrail.uiLayer.hikerProfile.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.coroutines.CoroutineIODispatcher
import com.sildian.apps.togetrail.domainLayer.hiker.UpdateHikerProfileUseCase
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HikerProfileEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @CoroutineIODispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val updateHikerProfileUseCase: UpdateHikerProfileUseCase,
) : ViewModel() {

    val hikerEditState: StateFlow<HikerEditState>
        get() = savedStateHandle.getStateFlow(
            key = KEY_SAVED_STATE_HIKER_EDIT,
            initialValue = HikerEditState.Initializing,
        )

    private val _hikerEditEvent = MutableSharedFlow<HikerProfileEditEvent>()
    val hikerEditEvent: SharedFlow<HikerProfileEditEvent> get() = _hikerEditEvent

    fun onInit(hiker: HikerUI) {
        savedStateHandle[KEY_SAVED_STATE_HIKER_EDIT] =
            HikerEditState.Initialized.NotEdited(hiker = hiker)
    }

    fun onAddPictureButtonClick() {
        viewModelScope.launch {
            _hikerEditEvent.emit(HikerProfileEditEvent.ShowImagePicker)
        }
    }

    fun onPictureSelected(uri: String) {
        savedStateHandle[KEY_SAVED_STATE_HIKER_EDIT] = hikerEditState.value.reduce(
            action = HikerProfileEditAction.UpdatePhoto(uri = uri)
        )
    }

    fun onNameFieldUpdated(text: String) {
        savedStateHandle[KEY_SAVED_STATE_HIKER_EDIT] = hikerEditState.value.reduce(
            action = HikerProfileEditAction.UpdateTextField(
                field = HikerProfileEditTextField.Name,
                value = text,
            )
        )
    }

    fun onBirthdayFieldClick() {
        viewModelScope.launch {
            _hikerEditEvent.emit(
                HikerProfileEditEvent.ShowDatePicker(
                    selectedDate = hikerEditState.value.data?.birthday
                )
            )
        }
    }

    fun onDateSelected(date: LocalDate) {
        savedStateHandle[KEY_SAVED_STATE_HIKER_EDIT] = hikerEditState.value.reduce(
            action = HikerProfileEditAction.UpdateBirthday(date = date)
        )
    }

    fun onHomeFieldClick() {
        viewModelScope.launch {
            _hikerEditEvent.emit(HikerProfileEditEvent.ShowLocationPicker)
        }
    }

    fun onDescriptionFieldUpdated(text: String) {
        savedStateHandle[KEY_SAVED_STATE_HIKER_EDIT] = hikerEditState.value.reduce(
            action = HikerProfileEditAction.UpdateTextField(
                field = HikerProfileEditTextField.Description,
                value = text,
            )
        )
    }

    fun onSaveMenuButtonClick() {
        viewModelScope.launch(coroutineDispatcher) {
            val editedState = hikerEditState.value as? HikerEditState.Initialized.Edited
                ?: run {
                    _hikerEditEvent.emit(HikerProfileEditEvent.NotifyProfileUpdateEmpty)
                    return@launch
                }
            if (editedState.errorFields.isNotEmpty()) {
                _hikerEditEvent.emit(HikerProfileEditEvent.NotifyProfileUpdateFieldsError)
                return@launch
            }
            _hikerEditEvent.emit(
                try {
                    updateHikerProfileUseCase(
                        hiker = editedState.hiker,
                        imageUrlToDeleteFromStorage = editedState.oldPhotoUrl,
                        imageUriToAddInStorage = editedState.newPhotoUri,
                    )
                    HikerProfileEditEvent.NotifyProfileUpdateSuccess
                } catch (e: Throwable) {
                    e.printStackTrace()
                    HikerProfileEditEvent.NotifyProfileUpdateError(e = e)
                }
            )
        }
    }

    companion object {
        const val KEY_SAVED_STATE_HIKER_EDIT = "KEY_SAVED_STATE_HIKER_EDIT"
    }
}