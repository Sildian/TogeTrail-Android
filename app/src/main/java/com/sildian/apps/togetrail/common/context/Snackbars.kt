package com.sildian.apps.togetrail.common.context

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sildian.apps.togetrail.R

fun Context.showSnackbar(
    view: View,
    message: String,
    intent: SnackbarIntent = SnackbarIntent.Neutral,
    duration: Int = Snackbar.LENGTH_LONG,
    anchor: View? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Snackbar.make(this, view, message, duration).apply {
        anchorView = anchor
        setAction(actionText) { onActionClick?.invoke() }
        setBackgroundTint(ContextCompat.getColor(this@showSnackbar, intent.backgroundColor))
        setTextColor(ContextCompat.getColor(this@showSnackbar, intent.textColor))
        setActionTextColor(ContextCompat.getColor(this@showSnackbar, intent.actionTextColor))
    }.show()
}

sealed class SnackbarIntent(
    @ColorRes val backgroundColor: Int,
    @ColorRes val textColor: Int,
    @ColorRes val actionTextColor: Int,
) {

    data object Neutral : SnackbarIntent(
        backgroundColor = R.color.colorPrimary,
        textColor = R.color.colorWhite,
        actionTextColor = R.color.colorSecondary,
    )

    data object Success : SnackbarIntent(
        backgroundColor = R.color.colorGreenUltraLight,
        textColor = R.color.colorGreenDark,
        actionTextColor = R.color.colorBlack,
    )

    data object Error : SnackbarIntent(
        backgroundColor = R.color.colorRedUltraLight,
        textColor = R.color.colorRedDark,
        actionTextColor = R.color.colorBlack,
    )
}