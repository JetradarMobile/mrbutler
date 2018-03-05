package com.jetradar.permissions

import io.reactivex.Observable

interface PermissionsHandler {
  fun checkPermissions(vararg permissions: String): Observable<PermissionInfo>
  fun requestPermissions(vararg permissions: String): Observable<PermissionInfo>
}
