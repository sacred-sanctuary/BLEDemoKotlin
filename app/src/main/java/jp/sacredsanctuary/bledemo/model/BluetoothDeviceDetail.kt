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
package jp.sacredsanctuary.bledemo.model

import android.bluetooth.BluetoothDevice

class BluetoothDeviceDetail(
    val deviceName: String,
    val deviceAddress: String,
    val deviceType: String,
    val deviceBondState: String,
    val bluetoothClass: String,
    val uuids: String
) {
    companion object {
        fun createInstance(device: BluetoothDevice): BluetoothDeviceDetail {
            return BluetoothDeviceDetail(
                    deviceName = device.name ?: "",
                    deviceAddress = device.address,
                    deviceType = device.type.toString(),
                    deviceBondState = device.bondState.toString(),
                    bluetoothClass = device.bluetoothClass.toString(),
                    uuids = device.uuids.contentToString()
            )
        }
    }
}
