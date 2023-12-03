package com.sildian.apps.togetrail.common.context

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

fun ComponentActivity.registerForSinglePermissionRequest(
    permission: Permission,
    callback: PermissionRequestLauncher.Callback,
): PermissionRequestLauncher =
    SinglePermissionRequestLauncher(
        permission = permission,
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) callback.onGranted() else callback.onDenied()
        },
        activity = this,
        callback = callback,
    )

fun ComponentActivity.registerForMultiplePermissionRequest(
    permissions: Array<Permission>,
    callback: PermissionRequestLauncher.Callback,
): PermissionRequestLauncher =
    MultiplePermissionRequestLauncher(
        permissions = permissions,
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted ->
            if (isGranted.containsValue(false)) callback.onDenied() else callback.onGranted()
        },
        activity = this,
        callback = callback,
    )

private fun Context.isPermissionGranted(permission: Permission): Boolean =
    Build.VERSION.SDK_INT < permission.minSdkRequiringPermission
            || ContextCompat.checkSelfPermission(this, permission.permissionName) == PackageManager.PERMISSION_GRANTED

abstract class PermissionRequestLauncher(
    private val callback: Callback,
) {

    fun launch() {
        when {
            isGranted() -> callback.onGranted()
            shouldShowRationale() -> callback.onShowRationale()
            else -> request()
        }
    }

    protected abstract fun isGranted(): Boolean

    protected abstract fun shouldShowRationale(): Boolean

    protected abstract fun request()

    interface Callback {
        fun onGranted()
        fun onDenied()
        fun onShowRationale()
    }
}

private class SinglePermissionRequestLauncher(
    private val permission: Permission,
    private val activityResultLauncher: ActivityResultLauncher<String>,
    private var activity: ComponentActivity?,
    callback: Callback,
) : PermissionRequestLauncher(
    callback = callback,
), DefaultLifecycleObserver {

    init {
        requireNotNull(activity).lifecycle.addObserver(this)
    }

    override fun isGranted(): Boolean =
        requireNotNull(activity).isPermissionGranted(permission = permission)

    override fun shouldShowRationale(): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireNotNull(activity),
            permission.permissionName,
        )

    override fun request() {
        activityResultLauncher.launch(permission.permissionName)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        activityResultLauncher.unregister()
        requireNotNull(activity).lifecycle.removeObserver(this)
        activity = null
        super.onDestroy(owner)
    }
}

private class MultiplePermissionRequestLauncher(
    private val permissions: Array<Permission>,
    private val activityResultLauncher: ActivityResultLauncher<Array<String>>,
    private var activity: ComponentActivity?,
    callback: Callback,
): PermissionRequestLauncher(
    callback = callback,
), DefaultLifecycleObserver {

    init {
        requireNotNull(activity).lifecycle.addObserver(this)
    }

    override fun isGranted(): Boolean {
        permissions.forEach { permission ->
            if (!requireNotNull(activity).isPermissionGranted(permission = permission)) {
                return false
            }
        }
        return true
    }

    override fun shouldShowRationale(): Boolean {
        permissions.forEach {  permission ->
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireNotNull(activity),
                    permission.permissionName,
            )) {
                return true
            }
        }
        return false
    }

    override fun request() {
        activityResultLauncher.launch(permissions.map { it.permissionName }.toTypedArray())
    }

    override fun onDestroy(owner: LifecycleOwner) {
        activityResultLauncher.unregister()
        requireNotNull(activity).lifecycle.removeObserver(this)
        activity = null
        super.onDestroy(owner)
    }
}

enum class Permission(val permissionName: String, val minSdkRequiringPermission: Int) {
    WriteExternalStorage(
        permissionName = Manifest.permission.WRITE_EXTERNAL_STORAGE,
        minSdkRequiringPermission = Build.VERSION_CODES.M,
    ),
    Camera(
        permissionName = Manifest.permission.CAMERA,
        minSdkRequiringPermission = Build.VERSION_CODES.M,
    ),
    AccessFineLocation(
        permissionName = Manifest.permission.ACCESS_FINE_LOCATION,
        minSdkRequiringPermission = Build.VERSION_CODES.M,
    ),
    AccessCoarseLocation(
        permissionName = Manifest.permission.ACCESS_COARSE_LOCATION,
        minSdkRequiringPermission = Build.VERSION_CODES.M,
    ),
    PostNotifications(
        permissionName = Manifest.permission.POST_NOTIFICATIONS,
        minSdkRequiringPermission = Build.VERSION_CODES.TIRAMISU,
    ),
}