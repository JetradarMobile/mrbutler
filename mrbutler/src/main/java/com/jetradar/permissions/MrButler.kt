/*
 * Copyright (C) 2018 JetRadar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jetradar.permissions

import android.annotation.SuppressLint
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class MrButler(
    private val permissionsHandler: PermissionsHandler,
    private val logger: ((message: String) -> Unit)? = null
) {

  fun require(vararg permissions: String): Completable =
      require(shouldRequestPermissions = true, permissions = *permissions)

  @SuppressLint("CheckResult")
  fun require(shouldRequestPermissions: Boolean, vararg permissions: String): Completable =
      (if (shouldRequestPermissions) requestEach(*permissions) else checkEach(*permissions))
          .toList()
          .flatMapCompletable { permissionCheckResults ->
            val deniedPermissions = permissionCheckResults.filterIsInstance<PermissionDenied>()
            if (deniedPermissions.isEmpty()) {
              Completable.complete()
            } else {
              Completable.error(PermissionsDeniedException("Denied permissions $deniedPermissions"))
            }
          }

  fun check(vararg permissions: String): Single<Boolean> = checkEach(*permissions)
      .toList()
      .map { permissionCheckResults -> permissionCheckResults.all { it is PermissionGranted } }

  fun checkSingle(permission: String): Single<PermissionCheckResult> =
      checkEach(permission).singleOrError()

  fun checkEach(vararg permissions: String): Observable<PermissionCheckResult> {
    logger?.invoke("Check ${permissions.joinToString()}")
    return permissionsHandler.checkPermissions(*permissions)
        .doOnNext { permissionCheckResult -> logger?.invoke("$permissionCheckResult") }
  }

  fun request(vararg permissions: String): Single<Boolean> =
      requestEach(*permissions)
          .toList()
          .map { permissionCheckResults -> permissionCheckResults.all { it is PermissionGranted } }

  fun requestSingle(permission: String): Single<PermissionCheckResult> =
      requestEach(permission).singleOrError()

  fun requestEach(vararg permissions: String): Observable<PermissionCheckResult> {
    logger?.invoke("Request ${permissions.joinToString()}")
    return permissionsHandler.requestPermissions(*permissions)
        .doOnNext { permissionCheckResult -> logger?.invoke("$permissionCheckResult") }
  }
}
