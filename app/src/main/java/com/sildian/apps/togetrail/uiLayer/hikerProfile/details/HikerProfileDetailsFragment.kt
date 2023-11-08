package com.sildian.apps.togetrail.uiLayer.hikerProfile.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.FragmentHikerProfileDetailsBinding
import com.sildian.apps.togetrail.uiLayer.hikerProfile.HikerProfileViewModel
import com.sildian.apps.togetrail.uiLayer.hikerProfile.HikerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HikerProfileDetailsFragment : Fragment() {

    private val hikerProfileViewModel: HikerProfileViewModel by activityViewModels()

    private var binding: FragmentHikerProfileDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        DataBindingUtil.inflate<FragmentHikerProfileDetailsBinding>(
            inflater,
            R.layout.fragment_hiker_profile_details,
            container,
            false,
        ).apply {
            lifecycleOwner = this@HikerProfileDetailsFragment
            hikerProfileViewModel = this@HikerProfileDetailsFragment.hikerProfileViewModel
            binding = this
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        collectStates()
        hikerProfileViewModel.loadAll()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun collectStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                hikerProfileViewModel.hikerState.collect(::onHikerState)
            }
        }
    }

    private fun initToolbar() {
        binding?.fragmentProfileToolbar?.title = ""
        binding?.fragmentProfileToolbar?.setNavigationIcon(R.drawable.ic_back_white)
        binding?.fragmentProfileToolbar?.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding?.fragmentProfileToolbar?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_edit_edit -> {
                    hikerProfileViewModel.onEditMenuButtonClick()
                    true
                }
                R.id.menu_chat_chat -> {
                    hikerProfileViewModel.onConversationMenuButtonClick()
                    true
                }
                else ->
                    false
            }
        }
    }

    private fun onHikerState(hikerState: HikerState) {
        binding?.fragmentProfileToolbar?.apply {
            menu.clear()
            when {
                hikerState is HikerState.Result && hikerState.hiker.isCurrentUser ->
                    inflateMenu(R.menu.menu_edit)
                hikerProfileViewModel.isUserConnected ->
                    inflateMenu(R.menu.menu_chat)
                else ->
                    Unit
            }
        }
    }

    companion object {

        const val TAG = "HikerProfileDetailsFragment"

        fun newInstance(): Fragment = HikerProfileDetailsFragment()
    }
}