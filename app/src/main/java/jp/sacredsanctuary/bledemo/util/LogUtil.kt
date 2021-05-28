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
/**
 * @file LogUtil.java
 * @note <br></br>
 * VERBOSE: adb shell setprop log.tag.BleDemo VERBOSE<br></br>
 * DEBUG:   adb shell setprop log.tag.BleDemo DEBUG<br></br>
 * INFO:    adb shell setprop log.tag.BleDemo INFO
 */
package jp.sacredsanctuary.bledemo.util

import android.util.Log

object LogUtil {
    private const val TAG = "BleDemo"
    private val VERBOSE = Log.isLoggable(TAG, Log.VERBOSE)
    private val DEBUG = Log.isLoggable(TAG, Log.DEBUG)
    fun V(ClassName: String, log: String) {
        if (VERBOSE) {
            Log.v(TAG, "[$ClassName] $log")
        }
    }

    fun D(ClassName: String, log: String) {
        if (VERBOSE) {
            Log.v(TAG, "[$ClassName] $log")
        }
    }

    fun I(ClassName: String, log: String) {
        if (VERBOSE) {
            Log.v(TAG, "[$ClassName] $log")
        }
    }

    fun W(ClassName: String, log: String) {
        if (VERBOSE) {
            Log.v(TAG, "[$ClassName] $log")
        }
    }

    fun E(ClassName: String, log: String) {
        if (DEBUG) {
            Log.e(TAG, "[$ClassName] $log")
        }
    }
}