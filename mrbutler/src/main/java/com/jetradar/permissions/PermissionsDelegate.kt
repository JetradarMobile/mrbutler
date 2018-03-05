package com.jetradar.permissions

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import java.lang.ref.WeakReference

abstract class PermissionsDelegate<in T : Any> : PermissionsHandler {
  private var componentRef: WeakReference<T>? = null

  private val attachComponentStream = PublishRelay.create<T>()
  private val requestPermissionResultSubjects = mutableMapOf<String, SingleSubject<PermissionInfo>>()

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

  override fun checkPermissions(vararg permissions: String): Observable<PermissionInfo> = component().flattenAsObservable { component ->
    permissions.map { permission ->
      PermissionInfo(
          name = permission,
          isGranted = isPermissionGranted(component, permission),
          shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale(component, permission)
      )
    }
  }

  @Suppress("CascadeIf")
  @SuppressLint("CheckResult")
  override fun requestPermissions(vararg permissions: String): Observable<PermissionInfo> = component().flatMapObservable { component ->
    val unrequestedPermissions = mutableListOf<String>()
    val requestPermissionResults = permissions.map { permission ->
      if (isPermissionGranted(component, permission)) {
        Single.just(PermissionInfo(name = permission, isGranted = true, shouldShowRequestPermissionRationale = false))
      } else if (isPermissionRevoked(component, permission)) {
        Single.just(PermissionInfo(name = permission, isGranted = false, shouldShowRequestPermissionRationale = false))
      } else {
        requestPermissionResultSubjects[permission] ?: SingleSubject.create<PermissionInfo>().also {
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
      val resultSubject = checkNotNull(requestPermissionResultSubjects.remove(permission)) { "Could not find corresponding result subject" }
      val component = checkNotNull(componentRef?.get()) { "Component not attached" }
      resultSubject.onSuccess(PermissionInfo(
          name = permission,
          isGranted = grantResults[index] == PackageManager.PERMISSION_GRANTED,
          shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale(component, permission)
      ))
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
