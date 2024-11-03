package com.kma_kit.smarthome.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kma_kit.smarthome.R
import com.kma_kit.smarthome.data.model.response.Device
import com.kma_kit.smarthome.databinding.ItemDeviceBinding

class DeviceAdapter(
    private val onAutoSwitchChange: (device: Device, isChecked: Boolean) -> Unit,
    private val onValueSwitchChange: (device: Device, isChecked: Boolean) -> Unit
) : ListAdapter<Device, DeviceAdapter.DeviceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(
            ItemDeviceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bindData(getItem(position), position)
    }

    inner class DeviceViewHolder(private val binding: ItemDeviceBinding) :
        ViewHolder(binding.root) {

        fun bindData(device: Device, position: Int) {
            binding.apply {
                deviceName.text = device.name
                deviceType.text = device.typeName
                autoSwitch.isChecked = device.is_auto
                valueSwitch.isChecked = device.value == 1.0
                imageIcon.setImageResource(
                    when (device.device_type) {
                        "bulb" -> R.drawable.light
                        "water" -> R.drawable.humidity
                        "fan" -> R.drawable.ic_fan
                        "humidity" -> R.drawable.humidity
                        else -> R.drawable.temprature
                    }
                )

                /*valueSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    device.value = if (isChecked) 1.0 else 0.0
                    if (buttonView.isPressed) {
                        onValueSwitchChange(device, isChecked)
                    }
                }*/

                valueSwitch.setOnClickListener {
                    val isChecked = valueSwitch.isChecked
                    Log.d("Device Update", "${device.id} $isChecked");
                    autoSwitch.isChecked = false
                    // Cập nhật trạng thái của thiết bị
                    device.value = if (isChecked) 1.0 else 0.0
                    // Gọi callback để thông báo về bên ngoài
                    onValueSwitchChange(device, isChecked)
                }
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Device>() {
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }
}