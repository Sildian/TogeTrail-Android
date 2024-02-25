package com.sildian.apps.togetrail.uiLayer.hikerProfile.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.context.SnackbarIntent
import com.sildian.apps.togetrail.common.context.navigateBack
import com.sildian.apps.togetrail.common.context.showSnackbar
import com.sildian.apps.togetrail.common.ui.dateTimePicker.DatePickerDialogFragment
import com.sildian.apps.togetrail.common.ui.imagePicker.ImagePickerBottomSheetDialogFragment
import com.sildian.apps.togetrail.databinding.FragmentHikerProfileEditBinding
import com.sildian.apps.togetrail.uiLayer.hikerProfile.HikerProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class HikerProfileEditFragment : Fragment() {

    private val hikerProfileViewModel: HikerProfileViewModel by activityViewModels()
    private val hikerProfileEditViewModel: HikerProfileEditViewModel by viewModels()

    private var binding: FragmentHikerProfileEditBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        DataBindingUtil.inflate<FragmentHikerProfileEditBinding>(
            inflater,
            R.layout.fragment_hiker_profile_edit,
            container,
            false,
        ).apply {
            lifecycleOwner = this@HikerProfileEditFragment
            hikerProfileEditViewModel = this@HikerProfileEditFragment.hikerProfileEditViewModel
            binding = this
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        collectEvents()
        if (savedInstanceState == null) {
            hikerProfileViewModel.hikerState.value.result?.also { hiker ->
                hikerProfileEditViewModel.onInit(hiker = hiker)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun initToolbar() {
        binding?.fragmentProfileInfoEditToolbar?.apply {
            title = getString(R.string.toolbar_hiker_my_profile)
            setNavigationIcon(R.drawable.ic_back_white)
            setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
            inflateMenu(R.menu.menu_save)
            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.menu_save_save) {
                    hikerProfileEditViewModel.onSaveMenuButtonClick()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun collectEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                hikerProfileEditViewModel.hikerEditEvent.collect(::onEvent)
            }
        }
    }

    private fun onEvent(event: HikerProfileEditEvent) {
        when (event) {
            is HikerProfileEditEvent.ShowImagePicker ->
                showImagePicker()
            is HikerProfileEditEvent.ShowDatePicker ->
                showDatePicker(selectedDate = event.selectedDate)
            is HikerProfileEditEvent.ShowLocationPicker ->
                showLocationPicker()
            is HikerProfileEditEvent.NotifyProfileUpdateEmpty ->
               notifyProfileUpdateEmpty()
            is HikerProfileEditEvent.NotifyProfileUpdateFieldsError ->
                notifyProfileUpdateFieldsError()
            is HikerProfileEditEvent.NotifyProfileUpdateError ->
                notifyProfileUpdateError()
            is HikerProfileEditEvent.NotifyProfileUpdateSuccess ->
                notifyProfileUpdateSuccess()
        }
    }

    private fun showImagePicker() {
        ImagePickerBottomSheetDialogFragment.show(
            fragmentManager = childFragmentManager,
            onPictureSelected = hikerProfileEditViewModel::onPictureSelected,
        )
    }

    private fun showDatePicker(selectedDate: LocalDate?) {
        DatePickerDialogFragment.show(
            fragmentManager = childFragmentManager,
            selectedDate = selectedDate,
            onDateSelected = hikerProfileEditViewModel::onDateSelected,
        )
    }

    private fun showLocationPicker() {
        TODO("Not implemented")
    }

    private fun notifyProfileUpdateEmpty() {
        requireContext().showSnackbar(
            view = requireView(),
            message = getString(R.string.message_no_update),
        )
    }

    private fun notifyProfileUpdateFieldsError() {
        requireContext().showSnackbar(
            view = requireView(),
            message = getString(R.string.message_text_fields_empty),
        )
    }

    private fun notifyProfileUpdateError() {
        requireContext().showSnackbar(
            view = requireView(),
            message = getString(R.string.message_save_failure),
            intent = SnackbarIntent.Error,
            actionText = getString(R.string.button_common_retry),
            onActionClick = hikerProfileEditViewModel::onSaveMenuButtonClick,
        )
    }

    private fun notifyProfileUpdateSuccess() {
        requireContext().showSnackbar(
            view = requireView(),
            message = getString(R.string.message_save_success),
            intent = SnackbarIntent.Success,
        )
        requireActivity().navigateBack()
    }

    companion object {

        const val TAG = "HikerProfileEditFragment"

        fun newInstance(): Fragment = HikerProfileEditFragment()
    }
}