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
