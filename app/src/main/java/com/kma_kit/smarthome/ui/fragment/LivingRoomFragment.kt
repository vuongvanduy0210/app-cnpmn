package com.kma_kit.smarthome.ui.fragment

import RootController
import UpdateDeviceRequest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kma_kit.smarthome.R
import com.kma_kit.smarthome.data.entity.DeviceEntity
import com.kma_kit.smarthome.data.model.response.Device
import com.kma_kit.smarthome.data.model.response.HomeResponse
import com.kma_kit.smarthome.ui.adapter.DeviceAdapter
import kotlinx.coroutines.launch
import retrofit2.Response

class LivingRoomFragment : Fragment() {
    private lateinit var deviceAdapter: DeviceAdapter
    private val listDevices = mutableListOf<Device>()
    private val rootController: RootController by activityViewModels()

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra("message")?.let { message ->
                Log.d("LivingRoomFragment", "Broadcast received: $message")
                if (isAdded) {
                    fetchAndDisplayDevices()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_living_room, container, false)

        // Khởi tạo RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewLivingRoom)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Khởi tạo Adapter với listener
        deviceAdapter = DeviceAdapter(
            onAutoSwitchChange = { device, isChecked ->
                onDeviceSwitchChanged(device, isChecked)
            },
            onValueSwitchChange = { device, isChecked ->
                onDeviceValueSwitchChanged(device, isChecked)
            }
        )
        recyclerView.adapter = deviceAdapter
        rootController.devices.observe(viewLifecycleOwner) { devices ->
            refreshDataRealTime(devices)
        }
        return view
    }
    private fun refreshDataRealTime(devices: List<DeviceEntity>) {
        // Cập nhật dữ liệu thời gian thực từ LiveData
        val newDevices = listDevices.map { oldDev ->
            val newDevice = devices.find { it.device_id == oldDev.id }
            oldDev.copy(
                id = newDevice?.device_id?: oldDev.id,
                name = oldDev.name,
                value = newDevice?.value?.toDouble() ?: oldDev.value,
                is_auto = newDevice?.is_auto ?: oldDev.is_auto,
            )
        }
        listDevices.clear()
        listDevices.addAll(newDevices)
        deviceAdapter.submitList(newDevices)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(broadcastReceiver, IntentFilter("MyDataUpdate"))
            fetchAndDisplayDevices()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isAdded) {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        }
    }

    private fun fetchAndDisplayDevices() {
        lifecycleScope.launch {
            try {
                val response: Response<HomeResponse> = ApiClient.api.getDevices()

                if (response.isSuccessful) {
                    val homeResponse = response.body()
                    homeResponse?.let { home ->
                        listDevices.clear() // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                        home.rooms.forEach { room ->
                            if (room.id == "9e5c0098-1274-4b66-9f0d-ac63d4256ff4") { // Sửa lại ID cho phù hợp với phòng khách
                                val filteredDevices = room.devices.filter { device ->
                                    device.device_type != "temperature" &&
                                            device.device_type != "humidity" &&
                                            device.device_type != "gas"
                                }
                                listDevices.addAll(filteredDevices)
                            }
                        }
                        deviceAdapter.submitList(listDevices)
                    }
                } else {
                    Log.e("LivingRoomFragment", "API call failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LivingRoomFragment", "Error fetching devices", e)
            }
        }
    }

    private fun onDeviceSwitchChanged(device: Device, isChecked: Boolean) {
        // Xử lý sự kiện switch isAuto
        Log.d("LivingRoomFragment", "Switch clicked for device ${device.name}, isChecked: $isChecked")
        lifecycleScope.launch {
            try {
                val response = ApiClient.api.updateDeviceState(
                    device.id,
                    UpdateDeviceRequest(isChecked, device.value)
                )
                if (response.isSuccessful) {
                    Log.d("LivingRoomFragment", "Device state updated successfully")
                } else {
                    Log.e("LivingRoomFragment", "Failed to update device state: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LivingRoomFragment", "Error updating device state", e)
            }
        }
    }

    private fun onDeviceValueSwitchChanged(device: Device, isChecked: Boolean) {
        // Xử lý sự kiện switch value
        Log.d("LivingRoomFragment", "Value Switch clicked for device ${device.name}, isChecked: $isChecked")
        lifecycleScope.launch {
            try {
                val newValue = if (isChecked) 1.0 else 0.0
                val response = ApiClient.api.updateDeviceState(
                    device.id,
                    UpdateDeviceRequest(device.is_auto, newValue)
                )
                if (response.isSuccessful) {
                    Log.d("LivingRoomFragment", "Device value updated successfully")
                } else {
                    Log.e("LivingRoomFragment", "Failed to update device value: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LivingRoomFragment", "Error updating device value", e)
            }
        }
    }
}
