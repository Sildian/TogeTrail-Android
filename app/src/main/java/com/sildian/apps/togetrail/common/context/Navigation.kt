package com.sildian.apps.togetrail.common.context

import android.app.Activity
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.navigateTo(fragment: Fragment, tag: String, @IdRes container: Int) {
    if (supportFragmentManager.findFragmentByTag(tag) == null) {
        supportFragmentManager
            .beginTransaction()
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