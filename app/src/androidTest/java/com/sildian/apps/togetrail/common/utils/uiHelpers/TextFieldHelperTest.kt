package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sildian.apps.togetrail.R
import org.junit.Test

import org.junit.Assert.*

class TextFieldHelperTest {

    @Test
    fun given_coucou_when_checkTextFieldIsNotEmpty_then_checkFieldIsValid() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.applicationContext.setTheme(R.style.AppTheme)
        val textField=TextInputEditText(context)
        val textFieldLayout=TextInputLayout(context)
        textField.setText("Coucou")
        TextFieldHelper.checkTextFieldIsNotEmpty(textField, textFieldLayout)
        assertTrue(textFieldLayout.error.isNullOrEmpty())
    }

    @Test
    fun given_nothing_when_checkTextFieldIsNotEmpty_then_checkFieldIsNotValid() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.applicationContext.setTheme(R.style.AppTheme)
        val textField=TextInputEditText(context)
        val textFieldLayout=TextInputLayout(context)
        TextFieldHelper.checkTextFieldIsNotEmpty(textField, textFieldLayout)
        assertFalse(textFieldLayout.error.isNullOrEmpty())
    }

    @Test
    fun given_3validTexts_when_checkAllTextFieldsAreNotEmpty_then_check_ResultIsTrue(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.applicationContext.setTheme(R.style.AppTheme)
        val textField1=TextInputEditText(context)
        val textField2=TextInputEditText(context)
        val textField3=TextInputEditText(context)
        val textFieldLayout1=TextInputLayout(context)
        val textFieldLayout2=TextInputLayout(context)
        val textFieldLayout3=TextInputLayout(context)
        textField1.setText("Mario")
        textField2.setText("Luigi")
        textField3.setText("Peach")
        val textFieldsAndLayouts= hashMapOf<TextInputEditText, TextInputLayout>()
        textFieldsAndLayouts[textField1]=textFieldLayout1
        textFieldsAndLayouts[textField2]=textFieldLayout2
        textFieldsAndLayouts[textField3]=textFieldLayout3
        assertTrue(TextFieldHelper.checkAllTextFieldsAreNotEmpty(textFieldsAndLayouts))
    }

    @Test
    fun given_1validTextAnd2nonValidTexts_when_checkAllTextFieldsAreNotEmpty_then_check_ResultIsFalse(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.applicationContext.setTheme(R.style.AppTheme)
        val textField1=TextInputEditText(context)
        val textField2=TextInputEditText(context)
        val textField3=TextInputEditText(context)
        val textFieldLayout1=TextInputLayout(context)
        val textFieldLayout2=TextInputLayout(context)
        val textFieldLayout3=TextInputLayout(context)
        textField1.setText("Mario")
        val textFieldsAndLayouts= hashMapOf<TextInputEditText, TextInputLayout>()
        textFieldsAndLayouts[textField1]=textFieldLayout1
        textFieldsAndLayouts[textField2]=textFieldLayout2
        textFieldsAndLayouts[textField3]=textFieldLayout3
        assertFalse(TextFieldHelper.checkAllTextFieldsAreNotEmpty(textFieldsAndLayouts))
    }
}