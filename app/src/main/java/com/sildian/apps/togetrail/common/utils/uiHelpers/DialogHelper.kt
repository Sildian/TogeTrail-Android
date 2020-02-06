package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Provides with some functions allowing to create dialogs
 ************************************************************************************************/

object DialogHelper {

    /**
     * Creates a progress dialog
     * @param context : the context
     * @return the dialog ready to be shown
     */

    fun createProgressDialog(context:Context): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setBackground(ContextCompat.getDrawable(
                context, R.drawable.shape_corners_round_color_primary))
            .setView(R.layout.dialog_progress)
            .setCancelable(false)
            .create()
    }
}