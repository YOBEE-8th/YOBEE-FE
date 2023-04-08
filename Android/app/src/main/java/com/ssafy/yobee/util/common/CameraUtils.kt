package com.ssafy.yobee.util.common

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object CameraUtils {
    // 퍼미션 확인해서 카메라 실행시키는 함수
    fun launchCamera(context: Context, launcher: ActivityResultLauncher<Intent>, file: File) {
        getCameraPermission(object : PermissionListener {
            override fun onPermissionGranted() {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val photoUri =
                    FileProvider.getUriForFile(context, "com.ssafy.yobee.fileprovider", file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                launcher.launch(intent)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(context, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 카메라 퍼미션
    private fun getCameraPermission(listener: PermissionListener) {
        TedPermission.create()
            .setPermissionListener(listener)
            .setDeniedMessage("권한을 허용해주세요")
            .setPermissions(android.Manifest.permission.CAMERA)
            .check()
    }

    // File -> Bitmap
    @Throws(IOException::class)
    fun getBitmapFromFile(context: Context, file: File): Bitmap {
        return if (Build.VERSION.SDK_INT >= 29) {
            val source: ImageDecoder.Source =
                ImageDecoder.createSource(context.contentResolver, Uri.fromFile(file))
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.fromFile(file))
        }
    }

    // Uri -> Bitmap
    @Throws(IOException::class)
    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    // Bitmap -> Uri
    @Throws(IOException::class)
    fun getUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            "title-${Calendar.getInstance().time}",
            null
        )
        return Uri.parse(path)
    }

    // 이미지 파일 생성
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path
            absolutePath
        }
    }
}