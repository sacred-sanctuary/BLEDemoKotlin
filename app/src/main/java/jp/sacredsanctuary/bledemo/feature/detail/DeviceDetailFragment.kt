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
package jp.sacredsanctuary.bledemo.feature.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import jp.sacredsanctuary.bledemo.R
import jp.sacredsanctuary.bledemo.databinding.FragmentDeviceDetailBinding
import jp.sacredsanctuary.bledemo.model.BluetoothDeviceDetail
import jp.sacredsanctuary.bledemo.util.LogUtil
import jp.sacredsanctuary.bledemo.view.MainActivityViewModel

class DeviceDetailFragment : Fragment(R.layout.fragment_device_detail) {
    private val sharedViewModel: MainActivityViewModel by activityViewModels()
    private val args: DeviceDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDeviceDetailBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }

        sharedViewModel.bluetoothDevice.observe(viewLifecycleOwner, {
            binding.bluetoothDeviceDetail = BluetoothDeviceDetail.createInstance(it).apply {
                LogUtil.V(ClassName, "bluetoothDevice.observe() [INF] this:$this")
            }
        })

        args.deviceAddress?.let { deviceAddress ->
            sharedViewModel.connectDeviceAddress(deviceAddress)
        }
    }

    companion object {
        private val ClassName = DeviceDetailFragment::class.java.simpleName
    }
}
