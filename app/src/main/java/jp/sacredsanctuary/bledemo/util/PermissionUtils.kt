/*
 * Copyright (C) 2021 Sacred Sanctuary Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.sacredsanctuary.bledemo.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.util.*

/**
 * Util class to handle permissions.
 */
object PermissionUtils {
    /**
     * Gets the list of unauthorized permissions listed in manifest.
     *
     * @param context A context of the current app
     * @return Returns the list of unauthorized permissions listed in manifest.
     */
    fun getUnauthorizedPermissionList(context: Context): Array<String> {
        val list: MutableList<String> = ArrayList()
        val permissionList = getPermissionList(context)
        permissionList?.forEach { permission ->
            val ret = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permission == "android.permission.SYSTEM_ALERT_WINDOW") {
                    true
                } else {
                    (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
                }
            } else {
                context.packageManager.checkPermission(permission, context.packageName) == PackageManager.PERMISSION_GRANTED
            }
            if (ret.not()) list.add(permission)
        }
        return list.toTypedArray()
    }

    /**
     * Returns the list of permissions listed in manifest.
     *
     * @param context A Context object used to access application manifest.
     * @return Returns the list of permissions listed in manifest.
     */
    private fun getPermissionList(context: Context): Array<String>? {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo = try {
            packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        } catch (e: PackageManager.NameNotFoundException) {
            return null
        }
        return packageInfo.requestedPermissions
    }
}
