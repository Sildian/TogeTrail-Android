package com.sildian.apps.togetrail.hiker.profileEdit

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import kotlinx.android.synthetic.main.fragment_profile_settings_edit.view.*

/*************************************************************************************************
 * Lets the user edit its profile's settings
 * @param hiker : the current user
 ************************************************************************************************/

class ProfileSettingsEditFragment(private val hiker: Hiker?=null) : BaseDataFlowFragment()
{

    /**********************************UI component**********************************************/

    private val emailText by lazy {layout.fragment_profile_settings_edit_text_email}
    private val registrationDateText by lazy {layout.fragment_profile_settings_edit_text_registration_date}
    private val resetPasswordButton by lazy {layout.fragment_profile_settings_edit_button_reset_password}
    private val deleteAccountButton by lazy {layout.fragment_profile_settings_edit_button_delete_account}

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile_settings_edit

    override fun initializeUI(){
        initializeChangePasswordButton()
        initializeDeleteAccountButton()
        refreshUI()
    }

    override fun refreshUI(){
        updateEmailText()
        updateRegistrationDateText()
    }

    private fun updateEmailText(){
        this.emailText.text=this.hiker?.email
    }

    private fun updateRegistrationDateText(){
        this.hiker?.registrationDate?.let { registrationDate ->
            this.registrationDateText.text =
                DateUtilities.displayDateShort(registrationDate)
        }
    }

    private fun initializeChangePasswordButton(){
        this.resetPasswordButton.setOnClickListener {
            requestResetUserPasswordConfirmation()
        }
    }

    private fun initializeDeleteAccountButton(){
        this.deleteAccountButton.setOnClickListener {
            requestDeleteUserAccountConfirmation()
        }
    }

    /******************************Reset password action*****************************************/

    private fun requestResetUserPasswordConfirmation(){
        val dialog=DialogHelper.createYesNoDialog(
            context!!,
            R.string.message_password_reset_confirmation_title,
            R.string.message_password_reset_confirmation_message,
            DialogInterface.OnClickListener { dialog, which ->
                if(which==DialogInterface.BUTTON_POSITIVE){
                    (activity as ProfileEditActivity).resetUserPassword()
                }
            })
        dialog.show()
    }

    /*******************************Delete account action****************************************/

    private fun requestDeleteUserAccountConfirmation(){
        val dialog=DialogHelper.createYesNoCriticalDialog(
            context!!,
            R.string.message_account_delete_confirmation_title,
            R.string.message_account_delete_confirmation_message,
            DialogInterface.OnClickListener { dialog, which ->
                if(which==DialogInterface.BUTTON_POSITIVE){
                    (activity as ProfileEditActivity).deleteUserAccount()
                }
            })
        dialog.show()
    }
}
