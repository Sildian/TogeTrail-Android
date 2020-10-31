package com.sildian.apps.togetrail.common.baseControllers

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper

/*************************************************************************************************
 * Base for all activities
 ************************************************************************************************/

abstract class BaseActivity : AppCompatActivity() {

    /*********************************UI components**********************************************/

    private var progressDialog: AlertDialog?=null

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        loadData()
        initializeUI()
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

    /*************************************Navigation*********************************************/

    open fun finishOk() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    open fun finishCancel() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    /**********************************Query results handling************************************/

    fun onSaveSuccess() {
        dismissProgressDialog()
        finishOk()
    }

    fun onQueryError(e: Exception) {
        dismissProgressDialog()
        DialogHelper.createInfoDialog(
            this,
            R.string.message_query_failure_title,
            R.string.message_query_failure_message
        ).show()
    }
}