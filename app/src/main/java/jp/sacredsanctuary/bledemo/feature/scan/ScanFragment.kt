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
package jp.sacredsanctuary.bledemo.feature.scan

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import jp.sacredsanctuary.bledemo.R
import jp.sacredsanctuary.bledemo.databinding.FragmentScanBinding
import jp.sacredsanctuary.bledemo.feature.scan.viewmodel.ScanViewModel
import jp.sacredsanctuary.bledemo.util.LogUtil
import jp.sacredsanctuary.bledemo.view.MainActivityViewModel

class ScanFragment : Fragment(R.layout.fragment_scan) {
    private val viewModel: ScanViewModel by viewModels()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                sharedViewModel.scanning(true)
            } else {
                permissionRequestDialog()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animation = AnimationUtils.loadAnimation(activity, R.anim.scanning)
        val binding = FragmentScanBinding.bind(view).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        sharedViewModel.bluetoothDeviceDataList.observe(viewLifecycleOwner, {
            LogUtil.V(ClassName, "observe() [INF] bluetoothDeviceDataList:${it}")
            if (it.isNotEmpty()) {
                findNavController().navigate(ScanFragmentDirections.actionScanToList(it.toTypedArray()))
            }
        })

        sharedViewModel.scanning.observe(viewLifecycleOwner, { scanning ->
            if (scanning) {
                binding.scanView.startAnimation(animation)
                viewModel.scanningText(R.string.scanning_bluetooth_low_energy)
            } else {
                binding.scanView.clearAnimation()
                viewModel.scanningText(R.string.could_not_search_any_device)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        when {
            PermissionChecker.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED -> {
                sharedViewModel.scanning(true)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                permissionRequestDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun permissionRequestDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.permission_message)
            .setPositiveButton(R.string.settings) { _, _ ->
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts(PACKAGE_SCHEME, requireContext().packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                // ignore
            }
            .create()
            .show()
    }

    companion object {
        private val ClassName = ScanFragment::class.java.simpleName
        private const val PACKAGE_SCHEME = "package"
    }
}
