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
package jp.sacredsanctuary.bledemo.view

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.navigation.fragment.NavHostFragment
import jp.sacredsanctuary.bledemo.R
import jp.sacredsanctuary.bledemo.bluetooth.BluetoothLowEnergyControllerCallback
import jp.sacredsanctuary.bledemo.bluetooth.IBluetoothLowEnergyControllerCallback
import jp.sacredsanctuary.bledemo.databinding.ActivityMainBinding
import jp.sacredsanctuary.bledemo.feature.scan.ScanFragment
import jp.sacredsanctuary.bledemo.service.BleServiceConnection
import jp.sacredsanctuary.bledemo.service.BluetoothLeService
import jp.sacredsanctuary.bledemo.util.LogUtil

class MainActivity : AppCompatActivity(), ServiceConnection {
    private val viewModel: MainActivityViewModel by viewModels()
    // Code to manage Service lifecycle.
    var bleServiceConnection: BleServiceConnection? = null
    private var bluetoothLowEnergyControllerCallback: IBluetoothLowEnergyControllerCallback? = null
    private var progressDialog: ProgressDialog? = null
    private var mainHandler: Handler? = null
    private val hasAccessFileLocationPermission
        get() = PermissionChecker.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtil.V(ClassName, "onCreate() [INF] ")
        super.onCreate(savedInstanceState)

        //FragmentManager.enableDebugLogging(true)
        mainHandler = Handler(Looper.getMainLooper())
        bluetoothLowEnergyControllerCallback = BluetoothLowEnergyControllerCallback(this)
        bleServiceConnection = BleServiceConnection(this)
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bleServiceConnection?.let {
            bindService(gattServiceIntent, it, BIND_AUTO_CREATE)
        }

        viewModel.scanning.observe(this, { scanning ->
            LogUtil.V(ClassName, "observe() [INF] scanning:${scanning}")
            if (scanning) {
                if (hasAccessFileLocationPermission) {
                    bleServiceConnection?.scanBluetoothLowEnergyDevice(SCAN_PERIOD)
                }
            }
        })

        viewModel.connectDeviceAddress.observe(this, { deviceAddress ->
            LogUtil.V(ClassName, "observe() [INF] deviceAddress:${deviceAddress}")
            if (deviceAddress.isNotEmpty()) {
                showProgressDialog(null, getString(R.string.progress_msg_connecting_bluetooth_low_energy))
                bleServiceConnection?.connect(deviceAddress)
            }
        })
    }

    override fun onResume() {
        LogUtil.V(ClassName, "onResume() [INF] ")
        super.onResume()
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        LogUtil.V(ClassName, "onServiceConnected() [INF] name:{$name}")
        if (bleServiceConnection?.isBluetoothLowEnergySupported == true) {
            bleServiceConnection?.setCallback(bluetoothLowEnergyControllerCallback)

            val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setSupportActionBar(binding.toolbar)
            binding.reload.setOnClickListener { onReload() }

            requestEnableBluetooth()
        } else {
            showBleNotSupported()
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        LogUtil.V(ClassName, "onServiceDisconnected() [INF] name:${name}")
        bleServiceConnection?.setCallback(null)
    }

    private fun requestEnableBluetooth() {
        LogUtil.V(ClassName, "requestEnableBluetooth() mBluetoothAdapter.isEnabled():"
                + bleServiceConnection?.isEnabled)
        if (bleServiceConnection?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(enableBtIntent)
        }
    }

    fun onConnectionCompleted() {
        dismissProgressDialog()
        bleServiceConnection?.device?.let { device ->
            viewModel.bluetoothDevice(device)
        }
    }

    fun onConnectionFailed() {
        dismissProgressDialog()
        showToastMsg(resources.getString(R.string.bluetooth_low_energy_connection_failed))
    }

    private fun showBleNotSupported() {
        AlertDialog.Builder(this)
            .setTitle(R.string.bluetooth_not_support)
            .setMessage(R.string.application_cannot_be_used_on_this_device)
            .setPositiveButton(R.string.ok) { _, _ ->
                finish()
            }
            .setOnCancelListener {
                finish()
            }
            .create()
            .show()
    }


    private fun showProgressDialog(title: String?, msg: String?) {
        if (isFinishing.not()) {
            progressDialog = ProgressDialog(this@MainActivity)
            progressDialog?.setCancelable(false)
            progressDialog?.setTitle(title)
            progressDialog?.setMessage(msg)
            progressDialog?.show()
        }
    }

    private fun dismissProgressDialog() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    private fun showToastMsg(msg: String?) {
        if (isFinishing.not()) {
            mainHandler?.post {
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onScanCompleted(results: Set<ScanResult?>?) {
        viewModel.scanning(false)
        viewModel.setBluetoothDeviceDataList(results)
    }

    private fun onReload() {
        bleServiceConnection?.disconnect()
        viewModel.setBluetoothDeviceDataList(null)
        val navHostFragment = supportFragmentManager.primaryNavigationFragment ?: return
        val fragment = navHostFragment.childFragmentManager.fragments[0]
        LogUtil.V(ClassName, "onServiceDisconnected() [INF] fragment:${fragment}")
        if (fragment is ScanFragment) {
            viewModel.scanning(true)
        } else {
            NavHostFragment.findNavController(fragment).popBackStack(
                    R.id.scan_fragment, false
            )
        }
    }

    companion object {
        private val ClassName = MainActivity::class.java.simpleName

        // Stops scanning after 5 seconds.
        private const val SCAN_PERIOD: Long = 5000
    }
}
