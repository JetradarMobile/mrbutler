package com.jetradar.permissions

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager

class PermissionsFragment : Fragment(), PermissionsHandler {
  private val permissionsDelegate = PermissionsFragmentDelegate()

  override fun onAttach(context: Context) {
    super.onAttach(context)
    permissionsDelegate.attach(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
  }

  override fun onDetach() {
    super.onDetach()
    permissionsDelegate.detach()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    permissionsDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  override fun checkPermissions(vararg permissions: String) =
      permissionsDelegate.checkPermissions(*permissions)

  @Suppress("CascadeIf")
  override fun requestPermissions(vararg permissions: String) =
      permissionsDelegate.requestPermissions(*permissions)

  companion object {
    private const val TAG = "com.jetradar.permissions.PermissionsFragment"

    fun with(activity: FragmentActivity) = with(activity.supportFragmentManager)

    fun with(fragmentManager: FragmentManager) = fragmentManager.run {
      findFragmentByTag(TAG) as PermissionsFragment? ?: PermissionsFragment().also {
        beginTransaction()
            .add(it, TAG)
            .commitNowAllowingStateLoss()
      }
    }
  }
}
