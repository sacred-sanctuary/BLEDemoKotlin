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
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import jp.sacredsanctuary.bledemo.util.LogUtil

/**
 * Callback for GATT Writing
 */
class BleGattCallback : BluetoothGattCallback() {
    private var callback: IBluetoothLowEnergyControllerCallback? = null

    /**
     * Sets an instance of [IBluetoothLowEnergyControllerCallback] to receive events.
     *
     * @param callback The new callback or null, if the callback should be unregistered.
     */
    fun setCallback(callback: IBluetoothLowEnergyControllerCallback?) {
        this.callback = callback
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        LogUtil.V(ClassName, "onConnectionStateChange() [INF] status:${status} newState:${newState}")
        callback?.onConnectionStateChange(gatt, status, newState)
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        LogUtil.V(ClassName, "onServicesDiscovered() [INF] status:${status}")
        callback?.onServicesDiscovered(gatt, status)
    }

    override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
        LogUtil.V(ClassName, "onMtuChanged() [INF] mtu=$mtu status=${status}")
        callback?.onMtuChanged(gatt, mtu, status)
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt,
                                      characteristic: BluetoothGattCharacteristic,
                                      status: Int) {
        LogUtil.I(ClassName, "onServicesDiscovered() [INF] status:${status}")
        callback?.onCharacteristicRead(gatt, characteristic, status)
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt,
                                       characteristic: BluetoothGattCharacteristic,
                                       status: Int) {
        LogUtil.I(ClassName, "onCharacteristicWrite() [INF] status:${status}")
        callback?.onCharacteristicWrite(gatt, characteristic, status)
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                         characteristic: BluetoothGattCharacteristic) {
        callback?.onCharacteristicChanged(gatt, characteristic)
    }

    companion object {
        private val ClassName = BleGattCallback::class.java.simpleName
    }
}
