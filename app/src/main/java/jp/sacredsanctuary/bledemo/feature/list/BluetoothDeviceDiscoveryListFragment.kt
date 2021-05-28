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
package jp.sacredsanctuary.bledemo.feature.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import jp.sacredsanctuary.bledemo.R
import jp.sacredsanctuary.bledemo.databinding.FragmentDeviceDiscoveryListBinding
import jp.sacredsanctuary.bledemo.feature.list.item.DeviceListItem
import jp.sacredsanctuary.bledemo.model.BluetoothDeviceData
import jp.sacredsanctuary.bledemo.util.LogUtil

class BluetoothDeviceDiscoveryListFragment : Fragment(R.layout.fragment_device_discovery_list) {
    private val args: BluetoothDeviceDiscoveryListFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()
        FragmentDeviceDiscoveryListBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.deviceList.adapter = groupAdapter
        }
        groupAdapter.apply {
            LogUtil.V(ClassName, "onViewCreated() [INF] args.deviceDataList:${args.deviceDataList.contentToString()}")
            update(args.deviceDataList.map {
                LogUtil.V(ClassName, "onViewCreated() [INF] it:${it}")
                DeviceListItem(it, this@BluetoothDeviceDiscoveryListFragment::onItemClicked)
            })
        }
    }

    private fun onItemClicked(item: BluetoothDeviceData) {
        val action = BluetoothDeviceDiscoveryListFragmentDirections.actionListToDetail()
        action.deviceAddress = item.bluetoothDevice.address
        findNavController().navigate(action)
    }

    companion object {
        private val ClassName = BluetoothDeviceDiscoveryListFragment::class.java.simpleName
    }
}
