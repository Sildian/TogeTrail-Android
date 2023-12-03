package com.sildian.apps.togetrail.common.utils.permissionsHelpers

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/*************************************************************************************************
 * Provides with some functions allowing to monitor permissions
 * * @deprecated : See [com.sildian.apps.togetrail.common.context.PermissionRequestLauncher]
 ************************************************************************************************/

@Deprecated("See [com.sildian.apps.togetrail.common.context.PermissionRequestLauncher]")
object PermissionsHelper {

    /**
     * Check if a permission is granted
     * @param context : the context
     * @param permissionName : the permission name
     * @return a boolean
     */

    @JvmStatic
    fun isPermissionGranted(context: Context, permissionName: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || ContextCompat.checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_GRANTED
    }
}