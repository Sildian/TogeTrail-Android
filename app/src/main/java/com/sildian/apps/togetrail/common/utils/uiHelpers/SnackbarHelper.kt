package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Provides with some functions allowing to create Snackbars
 ************************************************************************************************/

object SnackbarHelper {

    /**
     * Creates a simple Snackbar
     * @param messageView : the view allowing to show the snackbar
     * @param anchorView : the view on top of which the snackbar should appear, if needed
     * @param textId : the resId for the text
     * @return the Snackbar
     */

    @JvmStatic
    fun createSimpleSnackbar(messageView: View, anchorView: View?, textId: Int): Snackbar {
        val snackbar = Snackbar.make(messageView, textId, Snackbar.LENGTH_LONG)
        if (anchorView != null) {
            snackbar.anchorView = anchorView
        }
        return snackbar
    }

    /**
     * Creates a Snackbar with an action button
     * @param messageView : the view allowing to show the snackbar
     * @param anchorView : the view on top of which the snackbar should appear, if needed
     * @param textId : the resId for the text
     * @param actionButtonId : the resId for the action button text
     * @param actionCallback : the action callback
     * @return the Snackbar
     */

    @JvmStatic
    fun createSnackbarWithAction(messageView: View, anchorView: View?, textId: Int,
                                 actionButtonId: Int, actionCallback: ()-> Unit): Snackbar {

        val snackbar = Snackbar.make(messageView, textId, Snackbar.LENGTH_LONG)
            .setAction(actionButtonId) { actionCallback.invoke() }
            .setActionTextColor(ContextCompat.getColor(messageView.context, R.color.colorSecondary))
        if (anchorView != null) {
            snackbar.anchorView = anchorView
        }
        return snackbar
    }
}