package com.sildian.apps.togetrail.chat.ui.others

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.data.core.Message
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.databinding.DialogFragmentMessageWriteBinding

/*************************************************************************************************
 * Displays a BottomSheetDialogFragment allowing the user
 * to write and send a message to a chat space
 * @param messageWriteCallback : the callback used when the message is validated
 * @param messageToEdit : a message to edit if exists, if null a new message will be sent
 ************************************************************************************************/

class MessageWriteDialogFragment(
    private val messageWriteCallback: MessageWriteCallback,
    private val messageToEdit: Message? = null
)
    : BottomSheetDialogFragment()
{

    /*****************************************UI items*******************************************/

    private lateinit var binding: DialogFragmentMessageWriteBinding

    /****************************************Callbacks*******************************************/

    interface MessageWriteCallback {
        fun sendMessage(text: String)
        fun editMessage(message: Message, newText: String)
    }

    /****************************************Life cycle******************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        this.binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_message_write, container, false)
        this.binding.messageWriteDialogFragment = this
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (this.messageToEdit != null) {
            this.binding.dialogFragmentMessageWriteTextFieldMessage.setText(this.messageToEdit.text)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        (activity as BaseActivity<out ViewDataBinding>).hideKeyboard()
        super.onDismiss(dialog)
    }

    /***************************************UI monitoring****************************************/

    @Suppress("UNUSED_PARAMETER")
    fun onCancelMessageButtonClick(view: View) {
        this.binding.dialogFragmentMessageWriteTextFieldMessage.setText("")
        dismiss()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onValidateMessageButtonClick(view: View) {
        val text = this.binding.dialogFragmentMessageWriteTextFieldMessage.text.toString()
        if (text.isNotEmpty()) {
            if (this.messageToEdit == null) {
                this.messageWriteCallback.sendMessage(this.binding.dialogFragmentMessageWriteTextFieldMessage.text.toString())
            }
            else {
                this.messageWriteCallback.editMessage(this.messageToEdit, this.binding.dialogFragmentMessageWriteTextFieldMessage.text.toString())
            }
            this.binding.dialogFragmentMessageWriteTextFieldMessage.setText("")
            dismiss()
        }
    }
}