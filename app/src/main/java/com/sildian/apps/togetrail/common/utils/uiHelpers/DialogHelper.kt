package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
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

    @JvmStatic
    fun createProgressDialog(context:Context): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setBackground(AppCompatResources.getDrawable(
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
     * @param callback : the button click callback
     */

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    @JvmStatic
    fun createInfoDialog(context: Context, @StringRes titleId:Int,
                         @StringRes messageId:Int, callback: (()->Unit)? = null): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setBackground(AppCompatResources.getDrawable(
                context, R.drawable.shape_corners_round_color_primary))
            .setTitle(titleId)
            .setMessage(messageId)
            .setNeutralButton(R.string.button_common_ok) { dialog, which ->
                callback?.invoke()
            }
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

    @JvmStatic
    fun createYesNoDialog(context: Context, @StringRes titleId:Int, @StringRes messageId:Int,
                          listener:DialogInterface.OnClickListener): AlertDialog{
        return MaterialAlertDialogBuilder(context)
            .setBackground(AppCompatResources.getDrawable(
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

    @JvmStatic
    fun createYesNoCriticalDialog(context: Context, @StringRes titleId:Int, @StringRes messageId:Int,
                          listener:DialogInterface.OnClickListener): AlertDialog{
        return MaterialAlertDialogBuilder(context, R.style.AlertDialogCriticalStyle)
            .setBackground(AppCompatResources.getDrawable(
                context, R.drawable.shape_corners_round_color_primary))
            .setTitle(titleId)
            .setMessage(messageId)
            .setCancelable(false)
            .setIcon(R.drawable.ic_delete_forever_black)
            .setPositiveButton(R.string.button_common_yes, listener)
            .setNegativeButton(R.string.button_common_no, listener)
            .create()
    }

    /**
     * Creates a dialog requesting an info from the user
     * @param context : the context
     * @param titleId : the resId for the title
     * @param callback : the callback to catch the answer
     * @return the dialog ready to be shown
     */

    @JvmStatic
    fun createRequestInfoDialog(context: Context, @StringRes titleId: Int,
                                callback: (String?)->Unit): AlertDialog {

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_request_info, null)
        val infoTextField = view.findViewById<TextInputEditText>(R.id.dialog_request_info_text_field_info)

        return MaterialAlertDialogBuilder(context)
            .setBackground(AppCompatResources.getDrawable(context, R.drawable.shape_corners_round_color_primary))
            .setTitle(titleId)
            .setView(view)
            .setCancelable(false)
            .setNeutralButton(R.string.button_common_ok) { dialogInterface: DialogInterface, i: Int ->
                callback.invoke(infoTextField.text.toString())
            }
            .create()
    }
}