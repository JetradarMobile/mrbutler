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
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.v4.content.PermissionChecker

internal fun Context.isPermissionGranted(permission: String) =
    PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED

internal fun Context.isPermissionRevoked(permission: String) =
    VERSION.SDK_INT >= VERSION_CODES.M && packageManager.isPermissionRevokedByPolicy(permission, packageName)
