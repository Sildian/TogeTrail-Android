package com.sildian.apps.togetrail.chat.others

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.databinding.DialogFragmentMessageWriteBinding
import kotlinx.android.synthetic.main.dialog_fragment_message_write.view.*

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

    private lateinit var layout: View
    private lateinit var messageTextField: EditText

    /****************************************Callbacks*******************************************/

    interface MessageWriteCallback {
        fun sendMessage(text: String)
        fun editMessage(message: Message, newText: String)
    }

    /****************************************Life cycle******************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val binding: DialogFragmentMessageWriteBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_message_write, container, false)
        binding.messageWriteDialogFragment = this
        this.layout = binding.root
        return this.layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.messageTextField = this.layout.dialog_fragment_message_write_text_field_message
        if (this.messageToEdit != null) {
            this.messageTextField.setText(this.messageToEdit.text)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        (activity as BaseActivity).hideKeyboard()
        super.onDismiss(dialog)
    }

    /***************************************UI monitoring****************************************/

    @Suppress("UNUSED_PARAMETER")
    fun onCancelMessageButtonClick(view: View) {
        this.messageTextField.setText("")
        dismiss()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onValidateMessageButtonClick(view: View) {
        val text = this.messageTextField.text.toString()
        if (text.isNotEmpty()) {
            if (this.messageToEdit == null) {
                this.messageWriteCallback.sendMessage(this.messageTextField.text.toString())
            }
            else {
                this.messageWriteCallback.editMessage(this.messageToEdit, this.messageTextField.text.toString())
            }
            this.messageTextField.setText("")
            dismiss()
        }
    }
}