package com.sildian.apps.togetrail.common.baseControllers

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.permissionsHelpers.PermissionsCallback
import com.sildian.apps.togetrail.common.utils.permissionsHelpers.PermissionsHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper

/*************************************************************************************************
 * Base for all activities
 ************************************************************************************************/

abstract class BaseActivity<T: ViewDataBinding> : AppCompatActivity() {

    /**********************************Static items**********************************************/

    companion object {
        private const val TAG = "BaseActivity"
    }

    /*******************************Permissions items********************************************/

    private var permissionsCallback: PermissionsCallback? = null

    /*********************************UI components**********************************************/

    protected lateinit var binding: T
    private var progressDialog: AlertDialog? = null

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(this, getLayoutId())
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

    fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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

    /*************************************Permissions********************************************/

    fun requestPermissions(permissionsRequestCode: Int, permissions: Array<String>,
                           callback: PermissionsCallback, @StringRes explanationMessageResId: Int) {

        this.permissionsCallback = callback

        if (isAllPermissionsGranted(permissions)) {
            this.permissionsCallback?.onPermissionsGranted(permissionsRequestCode)
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowPermissionsExplanation(permissions)) {
                    DialogHelper.createInfoDialog(
                        this,
                        R.string.message_permission_requested_title,
                        explanationMessageResId
                    )
                    {
                        requestPermissions(permissions, permissionsRequestCode)
                    }.show()
                }
                else {
                    requestPermissions(permissions, permissionsRequestCode)
                }
            }
        }
    }

    private fun isAllPermissionsGranted(permissions: Array<String>): Boolean {
        permissions.forEach { permission ->
            if (!PermissionsHelper.isPermissionGranted(this, permission)) {
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun shouldShowPermissionsExplanation(permissions: Array<String>): Boolean {
        permissions.forEach { permission ->
            if (shouldShowRequestPermissionRationale(permission)) {
                return true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var allPermissionsGranted = true
        for (i in grantResults.indices) {
            when(grantResults[i]) {
                PackageManager.PERMISSION_GRANTED -> {
                    if (i < permissions.size) {
                        Log.d(TAG, "Permission '${permissions[i]}' was granted in '${this.javaClass.simpleName}'")
                    }
                }
                PackageManager.PERMISSION_DENIED -> {
                    if (i < permissions.size) {
                        Log.d(TAG, "Permission '${permissions[i]}' was denied in '${this.javaClass.simpleName}'")
                    }
                    allPermissionsGranted = false
                }
            }
        }
        if (allPermissionsGranted) {
            this.permissionsCallback?.onPermissionsGranted(requestCode)
        } else {
            this.permissionsCallback?.onPermissionsDenied(requestCode)
        }
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