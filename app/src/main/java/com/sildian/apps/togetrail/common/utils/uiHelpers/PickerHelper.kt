package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.views.DatePickerDialogFragmentOld
import com.sildian.apps.togetrail.common.views.TimePickerDialogFragmentOld
import java.util.*

/*************************************************************************************************
 * Provides with some functions allowing to populate texts with pickers
 ************************************************************************************************/

object PickerHelper {

    /**
     * Populates an editText with a DatePicker allowing to fetch a date
     * @param editText : the view holding the date
     * @param activity : the activity holding the view
     * @param initialValue : the initial date in the view
     */

    @JvmStatic
    fun populateEditTextWithDatePicker(editText: EditText,
                                       activity: AppCompatActivity, initialValue: Date?){
        if(initialValue!=null) {
            editText.setText(DateUtilities.displayDateShort(initialValue))
        }
        editText.setOnClickListener {
            DatePickerDialogFragmentOld(editText)
                .show(activity.supportFragmentManager, "datePicker")
        }
    }

    /**
     * Populates an editText with a TimePicker allowing to fetch a time
     * @param editText : the view holding the time
     * @param activity : the activity holding the view
     * @param initialValue : the initial time in the view
     */

    @JvmStatic
    fun populateEditTextWithTimePicker(editText: EditText,
                                       activity: AppCompatActivity, initialValue: Date?){
        if(initialValue!=null) {
            editText.setText(DateUtilities.displayTime(initialValue))
        }
        editText.setOnClickListener {
            TimePickerDialogFragmentOld(editText)
                .show(activity.supportFragmentManager, "timePicker")
        }
    }
}