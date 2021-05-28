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
package jp.sacredsanctuary.bledemo.feature.scan.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.sacredsanctuary.bledemo.R

/**
 * Store the information for BluetoothDeviceData.
 */
class ScanViewModel : ViewModel() {
    private val _scanningText = MutableLiveData<@StringRes Int>(R.string.scanning_bluetooth_low_energy)
    val scanningText: LiveData<Int> = _scanningText

    fun scanningText(@StringRes text: Int) {
        _scanningText.postValue(text)
    }
}

