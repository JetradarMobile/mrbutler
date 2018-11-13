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
import android.content.pm.PackageManager
import android.util.Log
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import java.lang.ref.WeakReference

abstract class PermissionsDelegate<in T : Any> : PermissionsHandler {
  private var componentRef: WeakReference<T>? = null

  private val attachComponentStream = PublishRelay.create<T>()
  private val requestPermissionResultSubjects = mutableMapOf<String, SingleSubject<PermissionCheckResult>>()

  abstract fun isPermissionGranted(component: T, permission: String): Boolean
  abstract fun isPermissionRevoked(component: T, permission: String): Boolean
  abstract fun shouldShowRequestPermissionRationale(component: T, permission: String): Boolean
  abstract fun performRequestPermissions(component: T, permissions: Array<String>, requestCode: Int)

  fun attach(component: T) {
    componentRef = WeakReference(component)
    attachComponentStream.accept(component)
  }

  fun detach() {
    componentRef?.clear()
    componentRef = null
  }

  override fun checkPermissions(vararg permissions: String): Observable<PermissionCheckResult> = component().flattenAsObservable { component ->
    permissions.map { permission ->
      if (isPermissionGranted(component, permission)) PermissionGranted(permission)
      else PermissionDenied(permission, shouldShowRequestPermissionRationale(component, permission))
    }
  }

  @Suppress("CascadeIf")
  @SuppressLint("CheckResult")
  override fun requestPermissions(vararg permissions: String): Observable<PermissionCheckResult> = component().flatMapObservable { component ->
    val unrequestedPermissions = mutableListOf<String>()
    val requestPermissionResults = permissions.map { permission ->
      if (isPermissionGranted(component, permission)) {
        Single.just(PermissionGranted(permission))
      } else if (isPermissionRevoked(component, permission)) {
        Single.just(PermissionDenied(permission, shouldShowRequestPermissionRationale = false))
      } else {
        requestPermissionResultSubjects[permission] ?: SingleSubject.create<PermissionCheckResult>().also {
          requestPermissionResultSubjects[permission] = it
          unrequestedPermissions.add(permission)
        }
      }
    }
    if (unrequestedPermissions.isNotEmpty()) {
      performRequestPermissions(component, unrequestedPermissions.toTypedArray(), PERMISSIONS_REQUEST_CODE)
    }
    Single.concat(requestPermissionResults).toObservable()
  }

  fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode != PERMISSIONS_REQUEST_CODE) return
    permissions.forEachIndexed { index, permission ->
      val resultSubject = requestPermissionResultSubjects.remove(permission)
      if (resultSubject == null) {
        Log.e("MrButler", "Could not find result subject for $permission")
        return@forEachIndexed
      }
      val component = checkNotNull(componentRef?.get()) { "Component not attached" }
      val isPermissionGranted = grantResults[index] == PackageManager.PERMISSION_GRANTED
      resultSubject.onSuccess(
          if (isPermissionGranted) PermissionGranted(permission)
          else PermissionDenied(permission, shouldShowRequestPermissionRationale(component, permission))
      )
    }
  }

  @SuppressLint("CheckResult")
  private fun component(): Single<T> =
      Single.fromCallable { checkNotNull(componentRef?.get()) }
          .onErrorResumeNext(attachComponentStream.firstOrError())

  private companion object {
    private const val PERMISSIONS_REQUEST_CODE = 17
  }
}
