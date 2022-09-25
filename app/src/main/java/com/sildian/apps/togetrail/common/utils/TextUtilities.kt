package com.sildian.apps.togetrail.common.utils

import androidx.core.util.PatternsCompat

fun String.isValidEmail(): Boolean =
    this.isNotBlank() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()