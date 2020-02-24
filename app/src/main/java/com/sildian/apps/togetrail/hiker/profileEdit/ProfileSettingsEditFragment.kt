package com.sildian.apps.togetrail.hiker.profileEdit

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.listeners.OnSaveDataListener
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import kotlinx.android.synthetic.main.fragment_profile_settings_edit.view.*

/*************************************************************************************************
 * Lets the user edit its profile's settings
 * @param hiker : the current user
 ************************************************************************************************/

class ProfileSettingsEditFragment(val hiker: Hiker?=null) :
    Fragment(),
    OnSaveDataListener
{

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG_FRAGMENT = "TAG_FRAGMENT"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val coordinatorLayout by lazy {layout.fragment_profile_settings_edit_coordinator_layout}
    private val emailText by lazy {layout.fragment_profile_settings_edit_text_email}
    private val registrationDateText by lazy {layout.fragment_profile_settings_edit_text_registration_date}
    private val newPasswordTextLayout by lazy {layout.fragment_profile_settings_edit_text_layout_password_new}
    private val newPasswordTextField by lazy {layout.fragment_profile_settings_edit_text_field_password_new}
    private val newPasswordCheckTextLayout by lazy {layout.fragment_profile_settings_edit_text_layout_password_new_check}
    private val newPasswordCheckTextField by lazy {layout.fragment_profile_settings_edit_text_field_password_new_check}
    private val changePasswordButton by lazy {layout.fragment_profile_settings_edit_button_change_password}
    private val deleteAccountButton by lazy {layout.fragment_profile_settings_edit_button_delete_account}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_profile_settings_edit, container, false)
        initializeAllUIComponents()
        return this.layout
    }

    /*****************************************Data***********************************************/

    override fun onSaveData() {
        //TODO implement
    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeEmailText()
        initializeRegistrationDateText()
        initializeChangePasswordButton()
        initializeDeleteAccountButton()
    }

    private fun initializeEmailText(){
        this.emailText.text=this.hiker?.email
    }

    private fun initializeRegistrationDateText(){
        this.registrationDateText.text=DateUtilities.displayDateShort(this.hiker?.registrationDate!!)
    }

    private fun initializeChangePasswordButton(){
        this.changePasswordButton.setOnClickListener {
            changePassword()
        }
    }

    private fun initializeDeleteAccountButton(){
        this.deleteAccountButton.setOnClickListener {
            deleteAccount()
        }
    }

    /******************************Change password action****************************************/

    private fun changePassword(){

        /*If the new password is valid, shows a dialog asking confirmation to the user*/

        if(checkNewPasswordIsValid()){
            this.newPasswordTextLayout.error=null
            this.newPasswordCheckTextLayout.error=null
            val dialog=DialogHelper.createYesNoDialog(
                context!!,
                R.string.message_password_confirmation_title,
                R.string.message_password_confirmation_message,
                DialogInterface.OnClickListener { dialog, which ->
                    if(which==DialogInterface.BUTTON_POSITIVE){
                        val newPassword=this.newPasswordTextField.text.toString()
                        (activity as ProfileEditActivity).updateUserPassword(newPassword)
                    }
                })
            dialog.show()

            /*Else, shows an error message*/

        }else{
            this.newPasswordTextLayout.error=
                resources.getString(R.string.message_password_not_valid_short)
            this.newPasswordCheckTextLayout.error=
                resources.getString(R.string.message_password_not_valid_short)
            Snackbar
                .make(
                    this.coordinatorLayout,
                    R.string.message_password_not_valid_long,
                    Snackbar.LENGTH_LONG)
                .show()
        }
    }

    private fun checkNewPasswordIsValid():Boolean{
        return when{
            !checkPasswordFieldsAreFilled()->false
            !checkPasswordFieldsMatch()->false
            else->true
        }
    }

    private fun checkPasswordFieldsAreFilled():Boolean{
        return when{
            this.newPasswordTextField.text.isNullOrEmpty() -> false
            this.newPasswordCheckTextField.text.isNullOrEmpty() -> false
            else -> true
        }
    }

    private fun checkPasswordFieldsMatch():Boolean{
        return this.newPasswordTextField.text.toString()==this.newPasswordCheckTextField.text.toString()
    }

    /*******************************Delete account action****************************************/

    private fun deleteAccount(){
        val dialog=DialogHelper.createYesNoCriticalDialog(
            context!!,
            R.string.message_account_delete_confirmation_title,
            R.string.message_account_delete_confirmation_message,
            DialogInterface.OnClickListener { dialog, which ->
                if(which==DialogInterface.BUTTON_POSITIVE){
                    (activity as ProfileEditActivity).deleteUser()
                }
            })
        dialog.show()
    }
}
