package com.ssafy.yobee.util.common

import android.os.Build
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

object NotificationUtils {
    fun getPermission(listener: PermissionListener) {
        if (Build.VERSION.SDK_INT >= 23) {
            TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedMessage("권한을 허용해주세요")
                .setPermissions(android.Manifest.permission.POST_NOTIFICATIONS).check()

        }
    }
}