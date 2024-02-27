package org.freedomtool.utils.nfc

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object PermissionUtil {
    const val REQUEST_CODE_MULTIPLE_PERMISSIONS = 100
    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null && permissions != null && permissions.size > 0) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    fun showRationale(activity: Activity?, permission: String?): Boolean {
        return if (activity != null && permission != null) {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        } else true
    }
}
