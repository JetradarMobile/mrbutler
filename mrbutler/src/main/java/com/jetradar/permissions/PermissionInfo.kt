package com.jetradar.permissions

data class PermissionInfo(
    val name: String,
    val isGranted: Boolean,
    val shouldShowRequestPermissionRationale: Boolean
)
