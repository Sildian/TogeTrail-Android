package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.content.Context
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sildian.apps.togetrail.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TextFieldHelperTest {

    private lateinit var context: Context

    @Before
    @Suppress("DEPRECATION")
    fun setup() {
        this.context = RuntimeEnvironment.application.applicationContext
        this.context.setTheme(R.style.AppTheme)
    }

    @Test
    fun given_coucou_when_checkTextFieldIsNotEmpty_then_checkFieldIsValid() {
        val textField= TextInputEditText(context)
        val textFieldLayout= TextInputLayout(context)
        textField.setText("Coucou")
        TextFieldHelper.checkTextFieldIsNotEmpty(textField, textFieldLayout)
        assertTrue(textFieldLayout.error.isNullOrEmpty())
    }

    @Test
    fun given_nothing_when_checkTextFieldIsNotEmpty_then_checkFieldIsNotValid() {
        val textField= TextInputEditText(context)
        val textFieldLayout= TextInputLayout(context)
        TextFieldHelper.checkTextFieldIsNotEmpty(textField, textFieldLayout)
        assertFalse(textFieldLayout.error.isNullOrEmpty())
    }

    @Test
    fun given_3validTexts_when_checkAllTextFieldsAreNotEmpty_then_check_ResultIsTrue(){
        val textField1= TextInputEditText(context)
        val textField2= TextInputEditText(context)
        val textField3= TextInputEditText(context)
        val textFieldLayout1= TextInputLayout(context)
        val textFieldLayout2= TextInputLayout(context)
        val textFieldLayout3= TextInputLayout(context)
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
        val textField1= TextInputEditText(context)
        val textField2= TextInputEditText(context)
        val textField3= TextInputEditText(context)
        val textFieldLayout1= TextInputLayout(context)
        val textFieldLayout2= TextInputLayout(context)
        val textFieldLayout3= TextInputLayout(context)
        textField1.setText("Mario")
        val textFieldsAndLayouts= hashMapOf<TextInputEditText, TextInputLayout>()
        textFieldsAndLayouts[textField1]=textFieldLayout1
        textFieldsAndLayouts[textField2]=textFieldLayout2
        textFieldsAndLayouts[textField3]=textFieldLayout3
        assertFalse(TextFieldHelper.checkAllTextFieldsAreNotEmpty(textFieldsAndLayouts))
    }
}