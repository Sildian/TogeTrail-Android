package com.sildian.apps.togetrail.common.views

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.DateUtilities
import java.util.*

/**************************************************************************************************
 * Provides a dialog fragment allowing the user to pick a Time
 * @Param callView : the view calling the timePickerDialogFragment
 *************************************************************************************************/

class TimePickerDialogFragment(val callView: AutoCompleteTextView)
    : DialogFragment(),
    TimePickerDialog.OnTimeSetListener
{

    /**UI components**/

    private lateinit var timePickerDialog: TimePickerDialog

    /**Data**/

    private val year=0
    private val month=0
    private val dayOfMonth=0
    private var hourOfDay=0
    private var minute=0

    /**Callbacks**/

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        initializeTime()
        this.timePickerDialog= TimePickerDialog(context, R.style.DatePickerTheme,
            this, hourOfDay, minute, true)
        return this.timePickerDialog
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val time= DateUtilities.getDateAndTime(year, month, dayOfMonth, hourOfDay, minute)
        val timeToDisplay=DateUtilities.displayTime(time)
        this.callView.setText(timeToDisplay)
        this.callView.clearFocus()
    }

    override fun onCancel(dialog: DialogInterface) {
        this.callView.setText("")
        this.callView.clearFocus()
        super.onCancel(dialog)
    }

    /**Initializes the TimePicker's date**/

    private fun initializeTime(){

        /*Sets the time to now by default*/

        val calendar= Calendar.getInstance()

        /*If the callView already holds a time, then sets it as the TimePicker's date*/

        if(!this.callView.text.isNullOrEmpty()){
            calendar.time= DateUtilities.getTimeFromString(this.callView.text.toString())?:Date()
        }

        /*Then updates the date*/

        this.hourOfDay=calendar.get(Calendar.HOUR_OF_DAY)
        this.minute=calendar.get(Calendar.MINUTE)
    }
}