package com.sildian.apps.togetrail.common.baseApplication

import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

/*************************************************************************************************
 * Base application class
 ************************************************************************************************/

@HiltAndroidApp
class BaseApplication: MultiDexApplication()