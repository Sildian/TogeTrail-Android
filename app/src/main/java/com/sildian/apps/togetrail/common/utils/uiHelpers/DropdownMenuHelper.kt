package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Provides with some functions allowing to populate dropdown menus
 ************************************************************************************************/

object DropdownMenuHelper {

    /**
     * Populates a dropdown menu with an array of strings
     * @param autoCompleteTextView : the view holding the menu
     * @param choice : the list of string to populate the menu
     * @param initialValue : the id of the initial value in the choice
     */

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    fun populateDropdownMenu(autoCompleteTextView: AutoCompleteTextView, choice:Array<String>, initialValue:Int){

        val adapter= ArrayAdapter(autoCompleteTextView.context, R.layout.item_dropdown_menu, choice)
        autoCompleteTextView.setAdapter(adapter)
        val currentText=autoCompleteTextView.adapter.getItem(initialValue)
        autoCompleteTextView.setText(currentText.toString(), false)
        autoCompleteTextView.tag=initialValue
        autoCompleteTextView.setOnItemClickListener { adapterView, view, position, id ->
            autoCompleteTextView.tag=position
        }
    }
}