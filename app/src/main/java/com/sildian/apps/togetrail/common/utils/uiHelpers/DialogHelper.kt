package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.cloudHelpers.RecyclerViewFirebaseHelper
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import com.sildian.apps.togetrail.trail.others.TrailVerticalAdapter
import com.sildian.apps.togetrail.trail.others.TrailVerticalViewHolder
import kotlinx.android.synthetic.main.dialog_trail_selection.view.*

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

    /**
     * Creates a dialog allowing to select a trail in order to add it to an event
     * @param activity : the activity consuming the dialog
     * @param callback : the callback allowing to consume the result of the dialog
     */

    fun createTrailSelectionDialog(
        activity: AppCompatActivity, callback:(trail:Trail)->Unit):AlertDialog{

        /*Inflates the view*/

        val view= LayoutInflater.from(activity)
            .inflate(R.layout.dialog_trail_selection, null)

        /*Creates the dialog*/

        val dialog=MaterialAlertDialogBuilder(activity)
            .setBackground(ContextCompat.getDrawable(
                activity, R.drawable.shape_corners_round_color_primary))
            .setTitle(R.string.label_event_available_trails)
            .setView(view)
            .setCancelable(false)
            .setNegativeButton(R.string.button_common_cancel) { dialog, which -> }
            .create()

        /*Sets the trailsRecyclerView*/

        val trailsRecyclerView=view.dialog_trail_selection_recycler_view_trails
        val trailsAdapter=TrailVerticalAdapter(
            RecyclerViewFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java, TrailFirebaseQueries.getTrails(), activity),
            object:TrailVerticalViewHolder.OnTrailClickListener{

                /*On trail click, invokes the callback*/

                override fun onTrailClick(trail: Trail) {
                    dialog.dismiss()
                    callback.invoke(trail)
                }
        })
        trailsRecyclerView.adapter=trailsAdapter

        /*Then returns the dialog*/

        return dialog
    }
}