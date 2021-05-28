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
package jp.sacredsanctuary.bledemo.service

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import jp.sacredsanctuary.bledemo.bluetooth.IBluetoothLowEnergyControllerCallback
import jp.sacredsanctuary.bledemo.service.BluetoothLeService.LocalBinder
import jp.sacredsanctuary.bledemo.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * Connection for controlling the BluetoothLeService.
 */
class BleServiceConnection(callback: ServiceConnection) : ServiceConnection {
    private var bluetoothLeService: BluetoothLeService? = null
    private val myCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private val mCallbackServiceConnection: ServiceConnection = callback

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        LogUtil.V(ClassName, "onServiceConnected() [INF] name:$name")
        bluetoothLeService = (service as LocalBinder).service
        if (bluetoothLeService?.initialize() == false) {
            LogUtil.E(ClassName, "Unable to initialize Bluetooth")
        }
        mCallbackServiceConnection.onServiceConnected(name, service)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        LogUtil.V(ClassName, "onServiceDisconnected() [INF] name:$name")
        bluetoothLeService = null
        mCallbackServiceConnection.onServiceDisconnected(name)
    }

    /**
     * Sets an instance of [IBluetoothLowEnergyControllerCallback] to receive events.
     *
     * @param callback The new callback or null, if the callback should be unregistered.
     */
    fun setCallback(callback: IBluetoothLowEnergyControllerCallback?) {
        bluetoothLeService?.setCallback(callback)
    }

    /**
     * Start Bluetooth LE scan.
     */
    fun scanBluetoothLowEnergyDevice(time: Long) {
        myCoroutineScope.launch {
            delay(WAIT_TIME)
            bluetoothLeService?.scanBluetoothLowEnergyDevice(time)
        }
    }

    /**
     * Initiate a connection to a Bluetooth GATT capable device.
     *
     * @param address The device address of the destination device.
     */
    fun connect(address: String?) {
        myCoroutineScope.launch {
            delay(WAIT_TIME)
            bluetoothLeService?.connect(address)
        }
    }

    /**
     * Disconnects an established connection, or cancels a connection attempt
     * currently in progress.
     */
    fun disconnect() {
        myCoroutineScope.launch {
            delay(WAIT_TIME)
            bluetoothLeService?.disconnect()
        }
    }

    /**
     * Close this Bluetooth GATT client.
     *
     * Application should call this method as early as possible after it is done with
     * this GATT client.
     */
    fun close() {
        myCoroutineScope.launch {
            delay(WAIT_TIME)
            bluetoothLeService?.close()
        }
    }

    /**
     * Discovers services offered by a remote device as well as their
     * characteristics and descriptors.
     */
    fun discoverServices() {
        myCoroutineScope.launch {
            delay(WAIT_TIME)
            bluetoothLeService?.discoverServices()
        }
    }

    /**
     * Enable or disable notifications/indications for a given characteristic.
     */
    fun setCharacteristicNotification() {
        myCoroutineScope.launch {
            delay(WAIT_TIME)
            bluetoothLeService?.setCharacteristicNotification()
        }
    }

    /**
     * Request an MTU size used for a given connection.
     */
    fun requestMtu(mtu: Int) {
        myCoroutineScope.launch {
            delay(WAIT_TIME)
            bluetoothLeService?.requestMtu(mtu)
        }
    }

    /**
     * Determines if bluetooth low energy is supported or not.
     *
     * @return Returns `true` if bluetooth low energy is supported, `false` otherwise.
     */
    val isBluetoothLowEnergySupported: Boolean
        get() {
            bluetoothLeService
                    ?: LogUtil.I(ClassName, "isBluetoothLowEnergySupported() return:false")
            return bluetoothLeService?.isBluetoothLowEnergySupported ?: false
        }

    /**
     * Return true if Bluetooth is currently enabled and ready for use.
     *
     * @return true if the local adapter is turned on
     */
    val isEnabled: Boolean get() = bluetoothLeService?.isEnabled ?: false

    /**
     * Return the remote bluetooth device this GATT client targets to
     *
     * @return remote bluetooth device
     */
    val device: BluetoothDevice? get() = bluetoothLeService?.device

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth
     */
    fun writeCharacteristic(serviceUuid: UUID, uuid: UUID, data: ByteArray) {
        myCoroutineScope.launch {
            delay(WAIT_TIME)
            bluetoothLeService?.writeCharacteristic(serviceUuid, uuid, data)
        }
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth
     */
    fun writeCharacteristic(serviceUuid: UUID, uuid: UUID, data: String) {
        myCoroutineScope.launch {
            delay(WAIT_TIME)
            bluetoothLeService?.writeCharacteristic(serviceUuid, uuid, data)
        }
    }

    companion object {
        private val ClassName = BleServiceConnection::class.java.simpleName

        // BLE API call after 10 millisecond.
        private const val WAIT_TIME: Long = 10
    }

}
