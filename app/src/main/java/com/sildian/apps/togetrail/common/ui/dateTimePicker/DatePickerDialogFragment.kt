package com.sildian.apps.togetrail.common.ui.dateTimePicker

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.sildian.apps.togetrail.R
import java.time.LocalDate

class DatePickerDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    @Suppress("deprecation")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val selectedDate = (arguments?.getSerializable(KEY_ARG_DATE) as? LocalDate)
            ?: LocalDate.now()
        return DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            this,
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth,
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        setFragmentResult(
            requestKey = KEY_REQUEST_DATE,
            result = bundleOf(
                KEY_RESULT_DATE to LocalDate.of(year, month + 1, dayOfMonth)
            ),
        )
        dismiss()
    }

    companion object {

        private const val TAG = "DatePickerDialogFragment"
        private const val KEY_REQUEST_DATE = "KEY_REQUEST_DATE"
        private const val KEY_ARG_DATE = "KEY_ARG_DATE"
        private const val KEY_RESULT_DATE = "KEY_RESULT_DATE"

        @Suppress("deprecation")
        fun show(
            fragmentManager: FragmentManager,
            selectedDate: LocalDate?,
            onDateSelected: (date: LocalDate) -> Unit,
        ) {
            if (fragmentManager.findFragmentByTag(TAG) == null) {
                DatePickerDialogFragment().apply {
                    arguments = bundleOf(KEY_ARG_DATE to selectedDate)
                    setFragmentResultListener(requestKey = KEY_REQUEST_DATE) { _, bundle ->
                        onDateSelected(bundle.getSerializable(KEY_RESULT_DATE) as LocalDate)
                    }
                }.show(fragmentManager, TAG)
            }
        }
    }
}