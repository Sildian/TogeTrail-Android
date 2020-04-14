package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.content.Context
import android.content.DialogInterface
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

    /**
     * Creates a dialog aiming to give an info to the user
     * @param context : the context
     * @param titleId : the resId for the title
     * @param messageId : the resId for the message
     */

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    fun createInfoDialog(context: Context, titleId:Int, messageId:Int):AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setBackground(ContextCompat.getDrawable(
                context, R.drawable.shape_corners_round_color_primary))
            .setTitle(titleId)
            .setMessage(messageId)
            .setNeutralButton(R.string.button_common_ok) { dialog, which ->  }
            .create()
    }

    /**
     * Creates a dialog requesting a yes / no answer from the user
     * @param context : the context
     * @param titleId : the resId for the title
     * @param messageId : the resId for the message
     * @param listener : the listener for the onClick callbacks
     * @return the dialog ready to be shown
     */

    fun createYesNoDialog(context: Context, titleId:Int, messageId:Int,
                          listener:DialogInterface.OnClickListener):AlertDialog{
        return MaterialAlertDialogBuilder(context)
            .setBackground(ContextCompat.getDrawable(
                context, R.drawable.shape_corners_round_color_primary))
            .setTitle(titleId)
            .setMessage(messageId)
            .setCancelable(false)
            .setPositiveButton(R.string.button_common_yes, listener)
            .setNegativeButton(R.string.button_common_no, listener)
            .create()
    }

    /**
     * Creates a dialog requesting a yes / no answer from the user, for critical purpose
     * @param context : the context
     * @param titleId : the resId for the title
     * @param messageId : the resId for the message
     * @param listener : the listener for the onClick callbacks
     * @return the dialog ready to be shown
     */

    fun createYesNoCriticalDialog(context: Context, titleId:Int, messageId:Int,
                          listener:DialogInterface.OnClickListener):AlertDialog{
        return MaterialAlertDialogBuilder(context, R.style.AlertDialogCriticalStyle)
            .setBackground(ContextCompat.getDrawable(
                context, R.drawable.shape_corners_round_color_primary))
            .setTitle(titleId)
            .setMessage(messageId)
            .setCancelable(false)
            .setIcon(R.drawable.ic_delete_forever_black)
            .setPositiveButton(R.string.button_common_yes, listener)
            .setNegativeButton(R.string.button_common_no, listener)
            .create()
    }
}