package com.jetradar.permissions

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.v4.content.PermissionChecker

internal fun Context.isPermissionGranted(permission: String) =
    PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED

internal fun Context.isPermissionRevoked(permission: String) =
    VERSION.SDK_INT >= VERSION_CODES.M && packageManager.isPermissionRevokedByPolicy(permission, packageName)
