package com.sildian.apps.togetrail.common.utils.permissionsHelpers

/*************************************************************************************************
 * Callback used to get permissions requests results
 * @deprecated : See [com.sildian.apps.togetrail.common.context.PermissionRequestLauncher]
 ************************************************************************************************/

@Deprecated("See [com.sildian.apps.togetrail.common.context.PermissionRequestLauncher]")
interface PermissionsCallback {
    fun onPermissionsGranted(permissionsRequestCode: Int)
    fun onPermissionsDenied(permissionsRequestCode: Int)
}