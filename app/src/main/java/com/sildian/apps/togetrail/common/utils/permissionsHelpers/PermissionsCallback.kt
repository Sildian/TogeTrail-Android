package com.sildian.apps.togetrail.common.utils.permissionsHelpers

/*************************************************************************************************
 * Callback used to get permissions requests results
 ************************************************************************************************/

interface PermissionsCallback {
    fun onPermissionsGranted(permissionsRequestCode: Int)
    fun onPermissionsDenied(permissionsRequestCode: Int)
}