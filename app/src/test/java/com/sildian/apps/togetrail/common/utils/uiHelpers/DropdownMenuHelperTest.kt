package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.content.Context
import android.widget.AutoCompleteTextView
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DropdownMenuHelperTest {

    private lateinit var context: Context

    @Before
    @Suppress("DEPRECATION")
    fun setup() {
        this.context = RuntimeEnvironment.application.applicationContext
    }

    @Test
    fun given_MarioLuigiPeach_when_populateDropdownMenu_then_checkSizeIs3AndInitialValue1IsLuigi() {
        val autoCompleteTextView= AutoCompleteTextView(this.context)
        val choice=arrayOf("Mario", "Luigi", "Peach")
        DropdownMenuHelper.populateDropdownMenu(autoCompleteTextView, choice, 1)
        assertEquals(3, autoCompleteTextView.adapter.count)
        assertEquals("Luigi", autoCompleteTextView.text.toString())
    }
}