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

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanResult
import android.content.Context
import jp.sacredsanctuary.bledemo.util.LogUtil
import jp.sacredsanctuary.bledemo.view.MainActivity
import java.util.*

/**
 * Create a new BluetoothLowEnergyControllerCallback.
 *
 * @param context A context of the current app
 */
class BluetoothLowEnergyControllerCallback(
        private val context: Context
) : IBluetoothLowEnergyControllerCallback {
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        LogUtil.I(ClassName, "onConnectionStateChange() [INF] status:$status newState:$newState")
        if (BluetoothProfile.STATE_CONNECTED == newState) {
            LogUtil.I(ClassName, "Connected to GATT server.")
            (context as MainActivity).bleServiceConnection?.discoverServices()
        } else if (BluetoothProfile.STATE_DISCONNECTED == newState) {
            LogUtil.I(ClassName, "Disconnected from GATT server.")
            (context as MainActivity).bleServiceConnection?.disconnect()
            context.onConnectionFailed()
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        LogUtil.I(ClassName, "onServicesDiscovered() [INF] status:$status")
        if (BluetoothGatt.GATT_SUCCESS == status) {
            (context as MainActivity).bleServiceConnection?.setCharacteristicNotification()
            context.bleServiceConnection?.requestMtu(MAX_MTU_SIZE)
        } else {
            (context as MainActivity).bleServiceConnection?.close()
        }
    }

    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        LogUtil.I(ClassName, "onMtuChanged() [INF] mtu=$mtu status=$status")
        if (BluetoothGatt.GATT_SUCCESS == status && MAX_MTU_SIZE == mtu) {
            (context as MainActivity).onConnectionCompleted()
        } else {
            (context as MainActivity).onConnectionFailed()
        }
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt,
                                      characteristic: BluetoothGattCharacteristic,
                                      status: Int) {
        if (characteristic.value.size > 32) {
            LogUtil.I(ClassName,
                    "onCharacteristicRead() [INF] status=" + status
                            + " uid=" + characteristic.uuid
                            + " val.length=" + characteristic.value.size)
        } else {
            LogUtil.I(ClassName,
                    "onCharacteristicRead() [INF] status=" + status
                            + " uid=" + characteristic.uuid
                            + " val=" + Arrays.toString(characteristic.value))
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            LogUtil.E(ClassName, "Read characteristic failure on $gatt $characteristic")
        }
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt,
                                       characteristic: BluetoothGattCharacteristic,
                                       status: Int) {
        if (characteristic.value.size > 32) {
            LogUtil.I(ClassName,
                    "onCharacteristicWrite() [INF] status=" + status
                            + " uid=" + characteristic.uuid
                            + " val.length=" + characteristic.value.size)
        } else {
            LogUtil.I(ClassName,
                    "onCharacteristicWrite() [INF] status=" + status
                            + " uid=" + characteristic.uuid
                            + " val=" + Arrays.toString(characteristic.value))
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            LogUtil.E(ClassName, "Write characteristic failure on $gatt $characteristic")
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt?,
                                         characteristic: BluetoothGattCharacteristic) {
        val uuid = characteristic.uuid
        LogUtil.I(ClassName, "onCharacteristicChanged() [INF] uuid:$uuid")
    }

    override fun onScanCompleted(results: Set<ScanResult?>?) {
        LogUtil.V(ClassName, "onScanCompleted() [INF] results:$results")
        (context as MainActivity).onScanCompleted(results)
    }

    companion object {
        private val ClassName = BluetoothLowEnergyControllerCallback::class.java.simpleName
        private const val MAX_MTU_SIZE = 512
    }
}
