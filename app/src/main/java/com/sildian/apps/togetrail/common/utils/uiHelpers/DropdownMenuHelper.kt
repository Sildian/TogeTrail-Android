package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.views.DatePickerDialogFragment
import java.util.*

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

    fun populateDropdownMenu(autoCompleteTextView: AutoCompleteTextView, choice:Array<String>, initialValue:Int){

        val adapter= ArrayAdapter<String>(autoCompleteTextView.context, R.layout.item_dropdown_menu, choice)
        autoCompleteTextView.setAdapter(adapter)
        val currentText=autoCompleteTextView.adapter.getItem(initialValue)
        autoCompleteTextView.setText(currentText.toString(), false)
        autoCompleteTextView.tag=initialValue
        autoCompleteTextView.setOnItemClickListener { adapterView, view, position, id ->
            autoCompleteTextView.tag=position
        }
    }

    /**
     * Populates a dropdown menu with a DatePicker allowing to fetch a date
     * @param autoCompleteTextView : the view holding the menu
     * @param activity : the activity holding the view
     * @param initialValue : the initial date in the view
     */

    fun populateDropdownMenuWithDatePicker(autoCompleteTextView: AutoCompleteTextView,
                                           activity:AppCompatActivity, initialValue: Date?){
        if(initialValue!=null) {
            autoCompleteTextView.setText(DateUtilities.displayDateShort(initialValue))
        }
        autoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                DatePickerDialogFragment(autoCompleteTextView)
                    .show(activity.supportFragmentManager, "datePicker")
            }
        }
    }
}