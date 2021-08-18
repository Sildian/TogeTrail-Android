package com.sildian.apps.togetrail.hiker.profileEdit

import android.content.DialogInterface
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.databinding.FragmentProfileSettingsEditBinding
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel

/*************************************************************************************************
 * Lets the user edit its profile's settings
 * @param hikerId : the hiker's id
 ************************************************************************************************/

class ProfileSettingsEditFragment(private val hikerId: String?=null) :
    BaseFragment<FragmentProfileSettingsEditBinding>()
{

    /*****************************************Data***********************************************/

    private lateinit var hikerViewModel: HikerViewModel

    /***********************************Data monitoring******************************************/

    override fun loadData() {
        initializeData()
        observeSaveRequestSuccess()
        observeRequestFailure()
        loadHiker()
    }

    private fun initializeData() {
        this.hikerViewModel= ViewModelProviders
            .of(this, ViewModelFactory)
            .get(HikerViewModel::class.java)
        this.binding.profileSettingsEditFragment = this
        this.binding.hikerViewModel = this.hikerViewModel
    }

    private fun observeSaveRequestSuccess() {
        this.hikerViewModel.saveRequestSuccess.observe(this) { success ->
            if (success) {
                handleSaveDataSuccess()
            }
        }
    }

    private fun observeRequestFailure() {
        this.hikerViewModel.requestFailure.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    private fun loadHiker() {
        this.hikerId?.let { hikerId ->
            this.hikerViewModel.loadHikerFromDatabase(hikerId)
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile_settings_edit

    @Suppress("UNUSED_PARAMETER")
    fun onChangePasswordButtonClick(view: View){
        requestResetUserPasswordConfirmation()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onDeleteAccountButtonClick(view:View){
        requestDeleteUserAccountConfirmation()
    }

    /******************************Profile settings actions**************************************/

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun requestResetUserPasswordConfirmation(){
        val dialog=DialogHelper.createYesNoDialog(
            context!!,
            R.string.message_password_reset_confirmation_title,
            R.string.message_password_reset_confirmation_message
        ) { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                this.baseActivity?.showProgressDialog()
                this.hikerViewModel.resetUserPassword()
            }
        }
        dialog.show()
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun requestDeleteUserAccountConfirmation(){
        val dialog=DialogHelper.createYesNoCriticalDialog(
            context!!,
            R.string.message_account_delete_confirmation_title,
            R.string.message_account_delete_confirmation_message
        ) { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                this.baseActivity?.showProgressDialog()
                this.hikerViewModel.deleteUserAccount()
            }
        }
        dialog.show()
    }

    private fun handleSaveDataSuccess(){
        this.baseActivity?.dismissProgressDialog()
    }
}
