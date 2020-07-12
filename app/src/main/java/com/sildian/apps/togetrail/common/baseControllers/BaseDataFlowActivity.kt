package com.sildian.apps.togetrail.common.baseControllers

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ListenerRegistration
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper

/*************************************************************************************************
 * Base activity for all activity aiming to load and save data
 ************************************************************************************************/

abstract class BaseDataFlowActivity : AppCompatActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "BaseDataFlowActivity"
    }

    /*********************************UI components**********************************************/

    private var progressDialog: AlertDialog?=null

    /**********************************Queries items*********************************************/

    protected var hikerQueryRegistration: ListenerRegistration? = null      //The listener registration for the hiker query
    protected var trailQueryRegistration: ListenerRegistration? = null      //The listener registration for the trail query
    protected var eventQueryRegistration: ListenerRegistration? = null      //The listener registration for the event query
    protected var trailsQueryRegistration: ListenerRegistration? = null     //The listener registration for the trails list query
    protected var eventsQueryRegistration: ListenerRegistration? = null     //The listener registration for the events list query

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        loadData()
        initializeUI()
    }

    override fun onDestroy() {
        this.hikerQueryRegistration?.remove()
        this.trailQueryRegistration?.remove()
        this.eventQueryRegistration?.remove()
        this.trailsQueryRegistration?.remove()
        this.eventsQueryRegistration?.remove()
        super.onDestroy()
    }

    /*********************************Data monitoring*******************************************/

    open fun loadData(){}

    open fun updateData(data: Any?){}

    open fun saveData(){}

    /************************************UI monitoring*******************************************/

    abstract fun getLayoutId(): Int

    open fun initializeUI(){}

    open fun refreshUI(){}

    fun showProgressDialog() {
        this.progressDialog = DialogHelper.createProgressDialog(this)
        this.progressDialog?.show()
    }

    fun dismissProgressDialog() {
        this.progressDialog?.dismiss()
    }

    /**************************************Queries***********************************************/

    /**
     * Handles query errors
     * @param e : the exception
     */

    fun handleQueryError(e: Exception) {
        Log.w(TAG, e.message.toString())
        dismissProgressDialog()
        DialogHelper.createInfoDialog(
            this,
            R.string.message_query_failure_title,
            R.string.message_query_failure_message
        ).show()
    }
}