package com.sildian.apps.togetrail.hiker.ui.profileEdit

import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.databinding.FragmentProfileSettingsEditBinding
import com.sildian.apps.togetrail.hiker.data.dataRequests.HikerChangeEmailAddressDataRequest
import com.sildian.apps.togetrail.hiker.data.dataRequests.HikerDeleteAccountDataRequest
import com.sildian.apps.togetrail.hiker.data.dataRequests.HikerResetPasswordDataRequest
import com.sildian.apps.togetrail.hiker.data.viewModels.HikerViewModel
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Lets the user edit its profile's settings
 * @param hikerId : the hiker's id
 ************************************************************************************************/

@AndroidEntryPoint
class ProfileSettingsEditFragment(private val hikerId: String?=null) :
    BaseFragment<FragmentProfileSettingsEditBinding>(),
        EmailAddressWriteDialogFragment.EmailAddressWriteCallback
{

    /*****************************************Data***********************************************/

    private val hikerViewModel: HikerViewModel by viewModels()

    /**********************************UI component**********************************************/

    private var emailAddressWriteDialogFragment: EmailAddressWriteDialogFragment? = null

    /***********************************Data monitoring******************************************/

    override fun initializeData() {
        this.binding.profileSettingsEditFragment = this
        this.binding.hikerViewModel = this.hikerViewModel
        observeDataRequestState()
    }

    override fun loadData() {
        lifecycleScope.launchWhenStarted {
            loadHiker()
        }
    }

    private fun observeDataRequestState() {
        this.hikerViewModel.dataRequestState.observe(this) { dataRequestState ->
            if (dataRequestState?.data is HikerChangeEmailAddressDataRequest
                || dataRequestState?.data is HikerResetPasswordDataRequest
                || dataRequestState?.data is HikerDeleteAccountDataRequest
            ) {
                dataRequestState.error?.let { e ->
                    onQueryError(e)
                } ?: run {
                    if (dataRequestState.data is HikerChangeEmailAddressDataRequest) {
                        handleEmailAddressChangeRequestSuccess()
                    } else {
                        handleSaveDataSuccess()
                    }
                }
            }
        }
    }

    private fun loadHiker() {
        this.hikerId?.let { hikerId ->
            this.hikerViewModel.loadHikerFlow(hikerId)
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile_settings_edit

    @Suppress("UNUSED_PARAMETER")
    fun onChangeEmailAddressButtonClick(view: View) {
        this.emailAddressWriteDialogFragment = EmailAddressWriteDialogFragment(this)
        this.emailAddressWriteDialogFragment?.show(childFragmentManager, "EmailAddressWriteDialogFragment")
    }

    @Suppress("UNUSED_PARAMETER")
    fun onChangePasswordButtonClick(view: View){
        requestResetUserPasswordConfirmation()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onDeleteAccountButtonClick(view:View){
        requestDeleteUserAccountConfirmation()
    }

    override fun validateEmailAddress(emailAddress: String) {
        requestChangeEmailAddressConfirmation(emailAddress)
    }

    /******************************Profile settings actions**************************************/

    private fun requestChangeEmailAddressConfirmation(emailAddress: String) {
        this.baseActivity?.showProgressDialog()
        this.hikerViewModel.changeUserEmailAddress(emailAddress)
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun requestResetUserPasswordConfirmation() {
        context?.let { context ->
            val dialog = DialogHelper.createYesNoDialog(
                context,
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
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun requestDeleteUserAccountConfirmation() {
        context?.let { context ->
            val dialog = DialogHelper.createYesNoCriticalDialog(
                context,
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
    }

    private fun handleEmailAddressChangeRequestSuccess() {
        this.baseActivity?.dismissProgressDialog()
        view?.let { view ->
            SnackbarHelper.createSimpleSnackbar(
                view,
                null,
                R.string.message_hiker_email_address_change_confirmation
            ).show()
        }
    }

    private fun handleSaveDataSuccess() {
        this.baseActivity?.dismissProgressDialog()
    }
}
