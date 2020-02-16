package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.content.Context
import android.os.Looper
import android.widget.AutoCompleteTextView
import androidx.test.core.app.ApplicationProvider
import org.junit.Test

import org.junit.Assert.*

class DropdownMenuHelperTest {

    @Test
    fun given_MarioLuigiPeach_when_populateDropdownMenu_then_checkSizeIs3AndInitialValue1IsLuigi() {
        Looper.prepare()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val autoCompleteTextView=AutoCompleteTextView(context)
        val choice=arrayOf("Mario", "Luigi", "Peach")
        DropdownMenuHelper.populateDropdownMenu(autoCompleteTextView, choice, 1)
        assertEquals(3, autoCompleteTextView.adapter.count)
        assertEquals("Luigi", autoCompleteTextView.text.toString())
    }
}