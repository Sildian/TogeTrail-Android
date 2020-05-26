package com.sildian.apps.togetrail.hiker.profileEdit

import android.content.DialogInterface
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.common.viewModels.ViewModelFactory
import com.sildian.apps.togetrail.databinding.FragmentProfileSettingsEditBinding
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel

/*************************************************************************************************
 * Lets the user edit its profile's settings
 * @param hiker : the current user
 ************************************************************************************************/

class ProfileSettingsEditFragment(private val hiker: Hiker?=null) : BaseDataFlowFragment()
{

    /*****************************************Data***********************************************/

    private lateinit var hikerViewModel: HikerViewModel

    /***********************************Data monitoring******************************************/

    override fun loadData() {
        this.hikerViewModel= ViewModelProviders
            .of(this, ViewModelFactory)
            .get(HikerViewModel::class.java)
        (this.binding as FragmentProfileSettingsEditBinding).profileSettingsEditFragment=this
        (this.binding as FragmentProfileSettingsEditBinding).hikerViewModel=this.hikerViewModel
        this.hiker?.id?.let { hikerId ->
            this.hikerViewModel.loadHikerFromDatabase(hikerId, null, this::handleQueryError)
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile_settings_edit

    override fun useDataBinding(): Boolean = true

    fun onChangePasswordButtonClick(view: View){
        requestResetUserPasswordConfirmation()
    }

    fun onDeleteAccountButtonClick(view:View){
        requestDeleteUserAccountConfirmation()
    }

    /******************************Reset password action*****************************************/

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
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

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
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
