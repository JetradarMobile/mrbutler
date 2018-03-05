package com.jetradar.permissions

import android.support.v4.app.Fragment

class PermissionsFragmentDelegate : PermissionsDelegate<Fragment>() {

  override fun isPermissionGranted(component: Fragment, permission: String) =
      component.context?.isPermissionGranted(permission) == true

  override fun isPermissionRevoked(component: Fragment, permission: String) =
      component.context?.isPermissionRevoked(permission) == true

  override fun shouldShowRequestPermissionRationale(component: Fragment, permission: String) =
      component.shouldShowRequestPermissionRationale(permission)

  override fun performRequestPermissions(component: Fragment, permissions: Array<String>, requestCode: Int) {
    component.requestPermissions(permissions, requestCode)
  }
}
