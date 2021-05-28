package jp.sacredsanctuary.bledemo.feature.list.item

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import jp.sacredsanctuary.bledemo.R
import jp.sacredsanctuary.bledemo.databinding.DeviceListItemBinding
import jp.sacredsanctuary.bledemo.model.BluetoothDeviceData

class DeviceListItem(
        private val deviceData: BluetoothDeviceData,
        private val onItemClick: (BluetoothDeviceData) -> Unit
) : BindableItem<DeviceListItemBinding>() {

    override fun getLayout() = R.layout.device_list_item

    override fun bind(viewBinding: DeviceListItemBinding, position: Int) {
        viewBinding.apply {
            deviceName.text = deviceData.bluetoothDevice.name
            deviceHardwareAddress.text = deviceData.bluetoothDevice.address
            root.setOnClickListener { onItemClick(deviceData) }
        }
    }

    override fun initializeViewBinding(view: View): DeviceListItemBinding {
        return DeviceListItemBinding.bind(view)
    }
}
