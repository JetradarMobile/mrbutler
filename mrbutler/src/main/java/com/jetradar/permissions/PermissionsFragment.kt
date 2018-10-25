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

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

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
