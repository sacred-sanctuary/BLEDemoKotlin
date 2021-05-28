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
package jp.sacredsanctuary.bledemo.bluetooth

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import jp.sacredsanctuary.bledemo.util.LogUtil
import java.util.*

/**
 * Helper class for BLE scan callback.
 */
class BleScanCallback : ScanCallback() {
    private val resultList = mutableSetOf<ScanResult>()
    private val scanResultList = mutableListOf<ScanResult>()

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
            LogUtil.V(ClassName, "onScanResult() [INF] result:$result")
            resultList.add(result)
        }
    }

    override fun onBatchScanResults(results: List<ScanResult>) {
        // In case onBatchScanResults are called due to buffer full, we want to collect all
        // scan results.
        scanResultList.addAll(results)
    }

    /**
     * Clear regular and batch scan results.
     */
    fun clear() {
        resultList.clear()
        scanResultList.clear()
    }

    /**
     * Return regular BLE scan results accumulated so far.
     */
    val scanResults: Set<ScanResult> get() = Collections.unmodifiableSet(resultList)

    companion object {
        private val ClassName = BleScanCallback::class.java.simpleName
    }
}
