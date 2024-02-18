package com.sildian.apps.togetrail.common.context

import android.app.Activity
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.sildian.apps.togetrail.R

fun FragmentActivity.navigateTo(fragment: Fragment, tag: String, @IdRes container: Int) {
    if (supportFragmentManager.findFragmentByTag(tag) == null) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.screen_transition_slide_in,
                R.anim.screen_transition_fade_out,
                R.anim.screen_transition_fade_in,
                R.anim.screen_transition_slide_out,
            )
            .replace(container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}

fun FragmentActivity.navigateBack(): Boolean =
    if (supportFragmentManager.backStackEntryCount > 1) {
        supportFragmentManager.popBackStack()
        false
    } else {
        setResult(Activity.RESULT_CANCELED)
        finish()
        true
    }