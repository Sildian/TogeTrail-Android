package com.sildian.apps.togetrail.uiLayer.hikerProfile.edit

enum class HikerProfileEditTextField(val isEmptyFieldAllowed: Boolean) {
    Name(isEmptyFieldAllowed = false),
    Description(isEmptyFieldAllowed = true),
}