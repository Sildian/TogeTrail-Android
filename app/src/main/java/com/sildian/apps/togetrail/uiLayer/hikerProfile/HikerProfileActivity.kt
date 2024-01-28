package com.sildian.apps.togetrail.uiLayer.hikerProfile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.ui.chatRoom.ChatActivity
import com.sildian.apps.togetrail.common.context.navigateBack
import com.sildian.apps.togetrail.common.context.navigateTo
import com.sildian.apps.togetrail.databinding.ActivityHikerProfileBinding
import com.sildian.apps.togetrail.event.ui.detail.EventActivity
import com.sildian.apps.togetrail.trail.ui.map.TrailActivity
import com.sildian.apps.togetrail.uiLayer.hikerProfile.details.HikerProfileDetailsFragment
import com.sildian.apps.togetrail.uiLayer.hikerProfile.edit.HikerProfileEditFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HikerProfileActivity : AppCompatActivity() {

    private val hikerProfileViewModel: HikerProfileViewModel by viewModels()

    private var binding: ActivityHikerProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_hiker_profile,
        )
        setOnBackPressedDispatcher()
        collectEvents()
        navigateToHikerProfileDetails()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun setOnBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(owner = this) {
            navigateBack()
        }
    }

    private fun collectEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                hikerProfileViewModel.navigationEvent.collect(::onNavigationEvent)
            }
        }
    }

    private fun onNavigationEvent(navigationEvent: HikerProfileNavigationEvent) {
        when (navigationEvent) {
            is HikerProfileNavigationEvent.NavigateToHikerProfileEdit ->
                navigateToHikerProfileEdit()
            is HikerProfileNavigationEvent.NavigateToTrail ->
                navigateToTrail(trailId = navigationEvent.trailId)
            is HikerProfileNavigationEvent.NavigateToEvent ->
                navigateToEvent(eventId = navigationEvent.eventId)
            is HikerProfileNavigationEvent.NavigateToConversation ->
                navigateToConversation(interlocutorId = navigationEvent.interlocutorId)
        }
    }

    private fun navigateToHikerProfileDetails() {
        navigateTo(
            fragment = HikerProfileDetailsFragment.newInstance(),
            tag = HikerProfileDetailsFragment.TAG,
            container = requireNotNull(binding?.activityHikerProfileFragmentContainer?.id),
        )
    }

    private fun navigateToHikerProfileEdit() {
        navigateTo(
            fragment = HikerProfileEditFragment.newInstance(),
            tag = HikerProfileEditFragment.TAG,
            container = requireNotNull(binding?.activityHikerProfileFragmentContainer?.id),
        )
    }

    private fun navigateToTrail(trailId: String) {
        startActivity(
            Intent(this, TrailActivity::class.java).apply {
                putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, TrailActivity.ACTION_TRAIL_SEE)
                putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ID, trailId)
            }
        )
    }

    private fun navigateToEvent(eventId: String) {
        startActivity(
            Intent(this, EventActivity::class.java).apply {
                putExtra(EventActivity.KEY_BUNDLE_EVENT_ID, eventId)
            }
        )
    }

    private fun navigateToConversation(interlocutorId: String) {
        startActivity(
            Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.KEY_BUNDLE_INTERLOCUTOR_ID, interlocutorId)
            }
        )
    }

    companion object {

        const val KEY_BUNDLE_HIKER_ID = "KEY_BUNDLE_HIKER_ID"

        fun newIntent(context: Context, hikerId: String): Intent =
            Intent(context, HikerProfileActivity::class.java).apply {
                putExtra(KEY_BUNDLE_HIKER_ID, hikerId)
            }
    }
}