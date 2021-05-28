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

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import jp.sacredsanctuary.bledemo.util.LogUtil
import java.util.*

/**
 * Controller used to operation Bluetooth Low Energy.
 */
class BluetoothLowEnergyController(private val context: Context) {
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothScanner: BluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private val backgroundHandler: Handler = Handler(Looper.getMainLooper())
    private val bleScanCallback: BleScanCallback = BleScanCallback()
    private var scanning = false
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothDeviceAddress: String? = null
    private var callback: IBluetoothLowEnergyControllerCallback? = null
    private val gattCallback = BleGattCallback()

    /**
     * Sets an instance of [IBluetoothLowEnergyControllerCallback] to receive events.
     *
     * @param callback The new callback or null, if the callback should be unregistered.
     */
    fun setCallback(callback: IBluetoothLowEnergyControllerCallback?) {
        this.callback = callback
        gattCallback.setCallback(callback)
    }

    /**
     * Determines if bluetooth low energy is supported or not.
     *
     * @return Returns `true` if bluetooth low energy is supported, `false` otherwise.
     */
    val isBluetoothLowEnergySupported: Boolean
        get() = context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)

    /**
     * Determines if bluetooth had enabled or not.
     *
     * @return Returns `true` if bluetooth had enabled, `false` otherwise.
     */
    val isEnabled: Boolean get() = bluetoothAdapter.isEnabled

    /**
     * Initiate a connection to a Bluetooth GATT capable device.
     *
     * @param address Bluetooth address as string
     * @return true, if the connection attempt was initiated successfully
     */
    fun connect(address: String?): Boolean {
        LogUtil.V(ClassName, "connect() [INF] address:$address")
        if (address.isNullOrEmpty()) {
            LogUtil.W(ClassName, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }
        // Previously connected device.  Try to reconnect.
        if (bluetoothDeviceAddress.isNullOrEmpty().not()
                && address == bluetoothDeviceAddress
                && bluetoothGatt != null
        ) {
            LogUtil.D(ClassName, "Trying to use an existing mBluetoothGatt for connection.")
            return bluetoothGatt?.connect() ?: false
        }
        val device = bluetoothAdapter.getRemoteDevice(address)
        LogUtil.V(ClassName, "connect() [INF] device:$device")

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        bluetoothDeviceAddress = address

        LogUtil.W(ClassName, "Trying to create a new connection.")
        return true
    }

    /**
     * Disconnects an established connection, or cancels a connection attempt
     * currently in progress.
     */
    fun disconnect() {
        bluetoothGatt ?: LogUtil.W(ClassName, "BluetoothAdapter not initialized")
        bluetoothGatt?.disconnect()
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    fun close() {
        bluetoothGatt ?: LogUtil.W(ClassName, "BluetoothAdapter not initialized")
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    /**
     * Return the remote bluetooth device this GATT client targets to
     *
     * @return remote bluetooth device
     */
    val device: BluetoothDevice?
        get() {
            bluetoothGatt ?: LogUtil.W(ClassName, "BluetoothAdapter not initialized")
            return bluetoothGatt?.device
        }

    /**
     * Start Bluetooth LE scan. The scan results will be delivered through `mBleScanCallback`.
     * For unfiltered scans, scanning is stopped on screen off to save power. Scanning is
     * resumed when screen is turned on again. To avoid this, do filetered scanning by
     * using proper [android.bluetooth.le.ScanFilter].
     */
    fun scanBluetoothLowEnergyDevice(time: Long) {
        LogUtil.V(ClassName, "scanBluetoothLowEnergyDevice() [INF] time:$time")
        if (scanning) return

        // Stops scanning after a pre-defined scan period.
        backgroundHandler.postDelayed({
            scanning = false
            LogUtil.V(ClassName, "scanBluetoothLowEnergyDevice() [INF] call stopScan() ")
            bluetoothScanner.stopScan(bleScanCallback)
            callback?.onScanCompleted(bleScanCallback.scanResults)
        }, time)
        scanning = true
        bleScanCallback.clear()
        LogUtil.V(ClassName, "scanBluetoothLowEnergyDevice() [INF] call startScan() ")
        bluetoothScanner.startScan(
                buildScanFilters(),
                buildScanSettings(),
                bleScanCallback
        )
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param serviceUuid The UUID of the requested service
     * @param uuid The UUID of the requested descriptor
     * @param data Data to write to Bluetooth
     */
    fun writeCharacteristic(serviceUuid: UUID, uuid: UUID, data: ByteArray) {
        bluetoothGatt ?: return
        if (data.isEmpty()) return
        LogUtil.V(ClassName, "writeCharacteristic() [INF] serviceUuid:${serviceUuid}, uuid:${uuid}, data.size:${data.size}")
        bluetoothGatt?.getService(serviceUuid)?.also { service ->
            service.getCharacteristic(uuid)?.also {
                it.value = data
                bluetoothGatt?.writeCharacteristic(it)
            }
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
        bluetoothGatt ?: return
        if (data.isEmpty()) return

        LogUtil.V(ClassName, "writeCharacteristic() [INF] serviceUuid:" + serviceUuid
                + " uuid:" + uuid + " data:" + data)
        bluetoothGatt?.getService(serviceUuid)?.also { service ->
            service.getCharacteristic(uuid)?.also {
                it.setValue(data)
                bluetoothGatt?.writeCharacteristic(it)
            }
        }
    }

    /**
     * Discovers services offered by a remote device as well as their
     * characteristics and descriptors.
     */
    fun discoverServices() {
        bluetoothGatt?.discoverServices()
    }

    /**
     * Enable notifications/indications for a given characteristic.
     */
    fun setCharacteristicNotification() {
        supportedGattServices ?: return
        for (gattService in supportedGattServices!!) {
            for (gattCharacteristic in gattService.characteristics) {
                LogUtil.V(ClassName,
                        "setCharacteristicNotification() [INF] Service:"
                                + gattService.uuid.toString()
                                + ", Characteristic:"
                                + gattCharacteristic.uuid.toString())
                setCharacteristicNotification(gattCharacteristic, true)
            }
        }
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification. False otherwise.
     */
    private fun setCharacteristicNotification(
            characteristic: BluetoothGattCharacteristic,
            enabled: Boolean
    ) {
        LogUtil.V(ClassName,
                "setCharacteristicNotification() [INF] characteristic:$characteristic")
        LogUtil.V(ClassName, "setCharacteristicNotification() [INF] enabled:$enabled")
        bluetoothGatt ?: LogUtil.W(ClassName, "BluetoothAdapter not initialized")

        bluetoothGatt?.setCharacteristicNotification(characteristic, enabled)

        // This is specific to Heart Rate Measurement.
        characteristic.getDescriptor(characteristic.uuid)?.also {
            it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt?.writeDescriptor(it)
        }
    }

    /**
     * Request an MTU size used for a given connection.
     *
     * @param mtu The new MTU size to request
     */
    fun requestMtu(mtu: Int) {
        LogUtil.V(ClassName, "requestMtu() [INF] ")
        bluetoothGatt?.requestMtu(mtu)
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after `BluetoothGatt#discoverServices()` completes successfully.
     *
     * @return A `List` of supported services.
     */
    private val supportedGattServices: List<BluetoothGattService>? get() = bluetoothGatt?.services

    /**
     * Return a List of [android.bluetooth.le.ScanFilter] objects to filter by Service UUID.
     */
    private fun buildScanFilters(): List<ScanFilter> {
        val scanFilters: MutableList<ScanFilter> = ArrayList()
        val builder = ScanFilter.Builder()
        // Comment out the below line to see all BLE devices around you
        builder.setServiceUuid(null)
        scanFilters.add(builder.build())
        return scanFilters
    }

    /**
     * Return a [android.bluetooth.le.ScanSettings] object set to use low power (to preserve
     * battery life).
     */
    private fun buildScanSettings(): ScanSettings {
        val builder = ScanSettings.Builder()
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        return builder.build()
    }

    companion object {
        private val ClassName = BluetoothLowEnergyController::class.java.simpleName
    }
}
