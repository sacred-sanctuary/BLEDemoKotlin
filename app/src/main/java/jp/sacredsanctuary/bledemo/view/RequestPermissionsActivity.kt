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
package jp.sacredsanctuary.bledemo.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import jp.sacredsanctuary.bledemo.R
import jp.sacredsanctuary.bledemo.util.LogUtil
import jp.sacredsanctuary.bledemo.util.PermissionUtils

/**
 * Activity that requests permissions needed for activities exported from file manager.
 */
open class RequestPermissionsActivity : Activity() {
    private var previousIntent: Intent? = null
    private var requiresPermissions: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previousIntent = intent.extras?.get(PREVIOUS_INTENT) as Intent?
        requiresPermissions = PermissionUtils.getUnauthorizedPermissionList(baseContext)
        if (savedInstanceState == null) {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantedResults: IntArray
    ) {
        if (permissions.isNotEmpty() && arePermissionsGranted(permissions, grantedResults)) {
            startPreviousActivity()
        } else {
            Toast.makeText(this, R.string.on_permission_read_file_manager, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>, grantResult: IntArray): Boolean {
        permissions.forEachIndexed { index, _ ->
            if (grantResult[index] != PackageManager.PERMISSION_GRANTED
                    && listOf(*requiresPermissions).contains(permissions[index])) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        if (requiresPermissions.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(requiresPermissions, REQUEST_ALL_PERMISSIONS)
            }
        } else {
            startPreviousActivity()
        }
    }

    private fun startPreviousActivity() {
        previousIntent?.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(previousIntent)
        finish()
    }

    companion object {
        private val ClassName = RequestPermissionsActivity::class.java.simpleName
        private const val PREVIOUS_INTENT = "previous_intent"
        private const val REQUEST_ALL_PERMISSIONS = 1
        fun startPermissionActivity(activity: Activity): Boolean {
            return startRequestPermissionActivity(activity, RequestPermissionsActivity::class.java)
        }

        private fun startRequestPermissionActivity(
                activity: Activity,
                newActivityClass: Class<*>?
        ): Boolean {
            val list = PermissionUtils.getUnauthorizedPermissionList(activity.baseContext)
            LogUtil.V(ClassName, "startRequestPermissionActivity() [INF] list:" + list.contentToString())
            if (list.isNotEmpty()) {
                val intent = Intent(activity, newActivityClass)
                intent.putExtra(PREVIOUS_INTENT, activity.intent)
                activity.startActivity(intent)
                activity.finish()
                return true
            }
            return false
        }
    }
}
