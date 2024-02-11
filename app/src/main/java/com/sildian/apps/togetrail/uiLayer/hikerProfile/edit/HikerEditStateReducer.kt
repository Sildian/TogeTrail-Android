package com.sildian.apps.togetrail.uiLayer.hikerProfile.edit

fun HikerEditState.reduce(action: HikerProfileEditAction): HikerEditState {
    if (this !is HikerEditState.Initialized) {
        return this
    }
    return when (action) {
        is HikerProfileEditAction.UpdateTextField ->
            updateTextField(action = action)
        is HikerProfileEditAction.UpdatePhoto ->
            updatePhoto(action = action)
        is HikerProfileEditAction.UpdateBirthday ->
            updateBirthday(action = action)
        is HikerProfileEditAction.UpdateHome ->
            updateHome(action = action)
    }
}

private fun HikerEditState.Initialized.updateTextField(
    action: HikerProfileEditAction.UpdateTextField
): HikerEditState.Initialized {
    val currentValue = when (action.field) {
        HikerProfileEditTextField.Name -> hiker.name
        HikerProfileEditTextField.Description -> hiker.description
    }
    if (action.value == currentValue) {
        return this
    }
    return HikerEditState.Initialized.Edited(
        hiker = when (action.field) {
            HikerProfileEditTextField.Name -> hiker.copy(name = action.value)
            HikerProfileEditTextField.Description -> hiker.copy(description = action.value)
        },
        oldPhotoUrl = oldPhotoUrl,
        newPhotoUri = newPhotoUri,
        errorFields = errorFields.let {
            val isCurrentFieldError =
                action.field.isEmptyFieldAllowed.not() && action.value.isBlank()
            if (isCurrentFieldError) {
                it.plus(action.field).distinct()
            } else {
                it.minus(action.field)
            }
        }
    )
}

private fun HikerEditState.Initialized.updatePhoto(
    action: HikerProfileEditAction.UpdatePhoto
): HikerEditState.Initialized {
    val oldPhotoUrl = hiker.photoUrl
    val newPhotoUri = action.uri
    return HikerEditState.Initialized.Edited(
        hiker = hiker.copy(photoUrl = newPhotoUri),
        oldPhotoUrl = oldPhotoUrl,
        newPhotoUri = newPhotoUri,
        errorFields = errorFields,
    )
}

private fun HikerEditState.Initialized.updateBirthday(
    action: HikerProfileEditAction.UpdateBirthday
): HikerEditState.Initialized =
    HikerEditState.Initialized.Edited(
        hiker = hiker.copy(birthday = action.date),
        oldPhotoUrl = oldPhotoUrl,
        newPhotoUri = newPhotoUri,
        errorFields = errorFields,
    )

private fun HikerEditState.Initialized.updateHome(
    action: HikerProfileEditAction.UpdateHome
): HikerEditState.Initialized =
    HikerEditState.Initialized.Edited(
        hiker = hiker.copy(home = action.location),
        oldPhotoUrl = oldPhotoUrl,
        newPhotoUri = newPhotoUri,
        errorFields = errorFields,
    )
