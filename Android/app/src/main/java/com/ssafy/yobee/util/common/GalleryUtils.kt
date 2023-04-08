package com.ssafy.yobee.util.common

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

object GalleryUtils {
    fun getGallery(context: Context, imageLauncher: ActivityResultLauncher<Intent>) { //사진 가져오기
        getPermission(object : PermissionListener {
            override fun onPermissionGranted() {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                imageLauncher.launch(intent)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(context, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getPermission(listener: PermissionListener) {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedMessage("권한을 허용해주세요")
                .setPermissions(android.Manifest.permission.READ_MEDIA_IMAGES)
                .check()
        } else {
            TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedMessage("권한을 허용해주세요")
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check()
        }

    }

    fun changeAbsolutelyPath(path: Uri?, context: Context): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)
        return result!!
    }
}