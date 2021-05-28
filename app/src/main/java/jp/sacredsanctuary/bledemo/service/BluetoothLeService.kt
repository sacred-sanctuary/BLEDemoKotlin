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

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import jp.sacredsanctuary.bledemo.R
import jp.sacredsanctuary.bledemo.bluetooth.BluetoothLowEnergyController
import jp.sacredsanctuary.bledemo.bluetooth.IBluetoothLowEnergyControllerCallback
import jp.sacredsanctuary.bledemo.util.LogUtil
import java.util.*

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
class BluetoothLeService : Service() {
    private var bleController: BluetoothLowEnergyController? = null

    private val binder: IBinder = LocalBinder()

    internal inner class LocalBinder : Binder() {
        val service: BluetoothLeService
            get() = this@BluetoothLeService
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close()
        return super.onUnbind(intent)
    }

    /**
     * Sets an instance of [IBluetoothLowEnergyControllerCallback] to receive events.
     *
     * @param callback The new callback or null, if the callback should be unregistered.
     */
    fun setCallback(callback: IBluetoothLowEnergyControllerCallback?) {
        bleController?.also {
            it.setCallback(callback)
        }
    }

    /**
     * Initializes a reference to the local Bluetooth controller .
     *
     * @return Return true if the initialization is successful.
     */
    fun initialize(): Boolean {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (bleController == null) {
            bleController = BluetoothLowEnergyController(applicationContext)
        }

        return bleController?.let {
            if (it.isEnabled.not() || it.isBluetoothLowEnergySupported.not()) {
                LogUtil.E(ClassName, "Unable to initialize BluetoothManager.")
                Toast.makeText(applicationContext, R.string.bluetooth_not_support, Toast.LENGTH_SHORT).show()
                false
            } else {
                true
            }
        } ?: false
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int,
     * int)`
     * callback.
     */
    fun connect(address: String?): Boolean {
        bleController ?: LogUtil.W(ClassName, "connect() [WAN] BluetoothAdapter not initialized or unspecified address.")
        return bleController?.connect(address) ?: return false
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int,
     * int)`
     * callback.
     */
    fun disconnect() {
        bleController ?: LogUtil.W(ClassName, "disconnect() [WAN] BluetoothAdapter not initialized")
        bleController?.disconnect()
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    fun close() {
        bleController ?: LogUtil.W(ClassName, "close() [WAN] BluetoothAdapter not initialized")
        bleController?.close()
        bleController = null
    }

    /**
     * Discovers services offered by a remote device as well as their
     * characteristics and descriptors.
     */
    fun discoverServices() {
        bleController ?: LogUtil.W(ClassName, "discoverServices() [WAN] BluetoothAdapter not initialized")
        bleController?.discoverServices()
    }

    /**
     * Enable or disable notifications/indications for a given characteristic.
     */
    fun setCharacteristicNotification() {
        bleController ?: LogUtil.W(ClassName, "setCharacteristicNotification() [WAN] BluetoothAdapter not initialized")
        bleController?.setCharacteristicNotification()
    }

    /**
     * Request an MTU size used for a given connection.
     *
     * @param mtu The new MTU size to request
     */
    fun requestMtu(mtu: Int) {
        bleController ?: LogUtil.W(ClassName, "requestMtu() [WAN] BluetoothAdapter not initialized")
        bleController?.requestMtu(mtu)
    }

    /**
     * Determines if bluetooth low energy is supported or not.
     *
     * @return Returns `true` if bluetooth low energy is supported, `false` otherwise.
     */
    val isBluetoothLowEnergySupported: Boolean
        get() {
            bleController ?: LogUtil.W(ClassName, "isBluetoothLowEnergySupported() [WAN] BluetoothAdapter not initialized")
            LogUtil.I(ClassName, "isBluetoothLowEnergySupported() isBluetoothLowEnergySupported:" + bleController?.isBluetoothLowEnergySupported)
            return bleController?.isBluetoothLowEnergySupported ?: false
        }

    /**
     * Return true if Bluetooth is currently enabled and ready for use.
     *
     * @return true if the local adapter is turned on
     */
    val isEnabled: Boolean
        get() {
            bleController ?: LogUtil.W(ClassName, "isEnabled() [WAN] BluetoothAdapter not initialized")
            return bleController?.isEnabled ?: false
        }

    /**
     * Return the remote bluetooth device this GATT client targets to
     *
     * @return remote bluetooth device
     */
    val device: BluetoothDevice?
        get() {
            bleController ?: LogUtil.W(ClassName, "device() [WAN] BluetoothAdapter not initialized")
            return bleController?.device
        }

    /**
     * Start Bluetooth LE scan.
     */
    fun scanBluetoothLowEnergyDevice(time: Long) {
        bleController ?: LogUtil.W(ClassName, "scanBluetoothLowEnergyDevice() [WAN] BluetoothAdapter not initialized")
        bleController?.scanBluetoothLowEnergyDevice(time)
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth.
     */
    fun writeCharacteristic(serviceUuid: UUID, uuid: UUID, data: ByteArray) {
        bleController ?: LogUtil.W(ClassName, "writeCharacteristic() [WAN] BluetoothAdapter not initialized")
        bleController?.writeCharacteristic(serviceUuid, uuid, data)
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth.
     */
    fun writeCharacteristic(serviceUuid: UUID, uuid: UUID, data: String) {
        bleController ?: LogUtil.W(ClassName, "writeCharacteristic() [WAN] BluetoothAdapter not initialized")
        bleController?.writeCharacteristic(serviceUuid, uuid, data)
    }

    companion object {
        private val ClassName = BluetoothLeService::class.java.simpleName
    }
}
