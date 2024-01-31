package com.sildian.apps.togetrail.common.ui.dateTimePicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.sildian.apps.togetrail.R
import java.time.LocalTime

class TimePickerDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    @Suppress("deprecation")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val selectedTime = (arguments?.getSerializable(KEY_ARG_TIME) as? LocalTime)
            ?: LocalTime.now()
        return TimePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            this,
            selectedTime.hour,
            selectedTime.minute,
            true,
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        setFragmentResult(
            requestKey = KEY_REQUEST_TIME,
            result = bundleOf(
                KEY_RESULT_TIME to LocalTime.of(hourOfDay, minute)
            ),
        )
        dismiss()
    }

    companion object {

        private const val TAG = "TimePickerDialogFragment"
        private const val KEY_REQUEST_TIME = "KEY_REQUEST_TIME"
        private const val KEY_ARG_TIME = "KEY_ARG_TIME"
        private const val KEY_RESULT_TIME = "KEY_RESULT_TIME"

        @Suppress("deprecation")
        fun show(
            fragmentManager: FragmentManager,
            selectedTime: LocalTime?,
            onTimeSelected: (time: LocalTime) -> Unit,
        ) {
            if (fragmentManager.findFragmentByTag(TAG) == null) {
                TimePickerDialogFragment().apply {
                    arguments = bundleOf(KEY_ARG_TIME to selectedTime)
                    show(fragmentManager, TAG)
                    setFragmentResultListener(requestKey = KEY_REQUEST_TIME) { _, bundle ->
                        onTimeSelected(bundle.getSerializable(KEY_RESULT_TIME) as LocalTime)
                    }
                }
            }
        }
    }
}