package com.sildian.apps.togetrail.common.context

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sildian.apps.togetrail.R

fun Context.showProgressDialog() {
    MaterialAlertDialogBuilder(this)
        .setBackground(AppCompatResources.getDrawable(this, R.drawable.shape_corners_round_color_primary))
        .setView(R.layout.dialog_progress)
        .setCancelable(false)
        .create()
        .show()
}

fun Context.showInfoDialog(
    title: String,
    message: String,
    onNeutralButtonClick: () -> Unit,
    icon: Drawable? = null,
) {
    MaterialAlertDialogBuilder(this)
        .setBackground(AppCompatResources.getDrawable(this, R.drawable.shape_corners_round_color_primary))
        .setTitle(title)
        .setMessage(message)
        .setIcon(icon)
        .setNeutralButton(R.string.button_common_ok) { _, _ -> onNeutralButtonClick() }
        .create()
        .show()
}

fun Context.showQuestionDialog(
    title: String,
    message: String,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit,
    icon: Drawable? = null,
) {
    MaterialAlertDialogBuilder(this)
        .setBackground(AppCompatResources.getDrawable(this, R.drawable.shape_corners_round_color_primary))
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setIcon(icon)
        .setPositiveButton(R.string.button_common_yes) { _, _ -> onPositiveButtonClick() }
        .setNegativeButton(R.string.button_common_no) { _, _ -> onNegativeButtonClick() }
        .create()
        .show()
}
