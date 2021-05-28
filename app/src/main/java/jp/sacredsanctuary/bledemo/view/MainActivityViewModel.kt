package jp.sacredsanctuary.bledemo.view

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.sacredsanctuary.bledemo.model.BluetoothDeviceData
import jp.sacredsanctuary.bledemo.util.LogUtil

/**
 * Store the information for MainActivity Data.
 */
class MainActivityViewModel : ViewModel() {
    private var _bluetoothDeviceDataList = MutableLiveData<List<BluetoothDeviceData>>()
    val bluetoothDeviceDataList: LiveData<List<BluetoothDeviceData>> = _bluetoothDeviceDataList

    private var _bluetoothDevice = MutableLiveData<BluetoothDevice>()
    val bluetoothDevice: LiveData<BluetoothDevice> = _bluetoothDevice

    private val _scanning = MutableLiveData(false)
    val scanning = _scanning

    private val _connectDeviceAddress = MutableLiveData("")
    val connectDeviceAddress = _connectDeviceAddress

    fun setBluetoothDeviceDataList(list: Set<ScanResult?>?) {
        val deviceList: MutableList<BluetoothDeviceData> = mutableListOf()
        list?.forEach { result: ScanResult? ->
            result?.device?.let { device ->
                val find = deviceList.find { it.bluetoothDevice.address == device.address } != null
                if (find.not()) {
                    LogUtil.V(ClassName, "setBluetoothDeviceDataList() [INF] result:$result")
                    deviceList.add(BluetoothDeviceData(device))
                }
            }
        }
        _bluetoothDeviceDataList.postValue(deviceList)
    }

    fun setBluetoothDeviceDataList(list: List<ScanResult>) {
        val deviceList: MutableList<BluetoothDeviceData> = mutableListOf()
        list.forEach { result: ScanResult ->
            val find = deviceList.find { it.bluetoothDevice.address == result.device.address } != null
            if (find.not()) {
                LogUtil.V(ClassName, "setBluetoothDeviceDataList() [INF] result:$result")
                deviceList.add(BluetoothDeviceData(result.device))
            }
        }
        _bluetoothDeviceDataList.postValue(deviceList)
    }

    fun bluetoothDevice(device: BluetoothDevice) {
        _bluetoothDevice.postValue(device)
    }

    fun scanning(scanning: Boolean) {
        _scanning.postValue(scanning)
    }

    fun connectDeviceAddress(deviceAddress: String) {
        _connectDeviceAddress.postValue(deviceAddress)
    }

    companion object {
        private val ClassName = MainActivityViewModel::class.java.simpleName
    }
}

