package com.sildian.apps.togetrail.common.context

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sildian.apps.togetrail.R

fun Context.showSnackbar(
    view: View,
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    anchor: View? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Snackbar.make(this, view, message, duration).apply {
        anchorView = anchor
        setAction(actionText) { onActionClick?.invoke() }
        setActionTextColor(ContextCompat.getColor(this@showSnackbar, R.color.colorSecondary))
    }.show()
}