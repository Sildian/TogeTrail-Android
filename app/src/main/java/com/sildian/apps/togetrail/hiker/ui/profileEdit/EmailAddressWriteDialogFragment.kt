package com.sildian.apps.togetrail.hiker.ui.profileEdit

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.utils.isValidEmail
import com.sildian.apps.togetrail.databinding.DialogFragmentEmailAddressWriteBinding

/*************************************************************************************************
 * Displays a BottomSheetDialogFragment allowing the user
 * to change his / her email address
 * @param emailAddressWriteCallback : the callback used when the email address is validated
 ************************************************************************************************/

class EmailAddressWriteDialogFragment(
    private val emailAddressWriteCallback: EmailAddressWriteCallback
) : BottomSheetDialogFragment()
{

    /*****************************************UI items*******************************************/

    private lateinit var binding: DialogFragmentEmailAddressWriteBinding

    /****************************************Callbacks*******************************************/

    interface EmailAddressWriteCallback {
        fun validateEmailAddress(emailAddress: String)
    }

    /****************************************Life cycle******************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        this.binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_email_address_write, container, false)
        this.binding.emailAddressWriteDialogFragment = this
        initializeEmailTextField()
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        (activity as BaseActivity<out ViewDataBinding>).hideKeyboard()
        super.onDismiss(dialog)
    }

    /*************************************UI initialization**************************************/

    private fun initializeEmailTextField() {
        this.binding.dialogFragmentEmailAddressWriteButtonValidate.isEnabled = false
        this.binding.dialogFragmentEmailAddressWriteTextFieldEmail
            .doOnTextChanged { text, start, before, count ->
                this.binding.dialogFragmentEmailAddressWriteButtonValidate.isEnabled =
                    text?.toString()?.isValidEmail() == true
            }
    }

    /***************************************UI monitoring****************************************/

    @Suppress("UNUSED_PARAMETER")
    fun onValidateEmailAddressButtonClick(view: View) {
        val emailAddress = this.binding.dialogFragmentEmailAddressWriteTextFieldEmail.text.toString()
        this.emailAddressWriteCallback.validateEmailAddress(emailAddress)
        dismiss()
    }
}