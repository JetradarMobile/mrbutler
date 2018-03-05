package com.jetradar.permissions

import android.app.Activity
import android.support.v4.app.ActivityCompat

class PermissionsActivityDelegate : PermissionsDelegate<Activity>() {

  override fun isPermissionGranted(component: Activity, permission: String) =
      component.isPermissionGranted(permission)

  override fun isPermissionRevoked(component: Activity, permission: String) =
      component.isPermissionRevoked(permission)

  override fun shouldShowRequestPermissionRationale(component: Activity, permission: String) =
      ActivityCompat.shouldShowRequestPermissionRationale(component, permission)

  override fun performRequestPermissions(component: Activity, permissions: Array<String>, requestCode: Int) {
    ActivityCompat.requestPermissions(component, permissions, requestCode)
  }
}
