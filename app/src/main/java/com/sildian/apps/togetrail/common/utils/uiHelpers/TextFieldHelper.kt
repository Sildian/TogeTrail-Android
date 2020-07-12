package com.sildian.apps.togetrail.common.utils.uiHelpers

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Provides with some functions allowing to manipulate text fields
 ************************************************************************************************/

object TextFieldHelper {

    /**
     * Checks that a text field is not empty
     * @param textField : the text field
     * @param textFieldLayout : the text field layout
     * @return true if the field is valid, false otherwise
     */

    @JvmStatic
    fun checkTextFieldIsNotEmpty(textField: TextInputEditText, textFieldLayout: TextInputLayout):Boolean{
        return if(textField.text.isNullOrEmpty()){
            textFieldLayout.error=textField.context.getString(R.string.message_text_field_empty)
            false
        }
        else{
            textFieldLayout.error=null
            true
        }
    }

    /**
     * Checks that a full set of text fields are not empty
     * @param textFieldsAndLayouts : a hashMap with each text field as the key and each related layout as the value
     * @return true if all text fields are valid, false otherwise
     */

    @JvmStatic
    fun checkAllTextFieldsAreNotEmpty(textFieldsAndLayouts:HashMap<TextInputEditText, TextInputLayout>):Boolean{
        var isValid=true
        textFieldsAndLayouts.keys.forEach { textField ->
            if(!checkTextFieldIsNotEmpty(textField, textFieldsAndLayouts.getValue(textField))){
                isValid=false
            }
        }
        return isValid
    }
}