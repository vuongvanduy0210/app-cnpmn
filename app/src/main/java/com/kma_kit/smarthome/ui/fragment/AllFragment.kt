package com.kma_kit.smarthome.ui.fragment

import ApiClient
import RootController
import UpdateDeviceRequest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
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

class AllFragment : Fragment() {

    private lateinit var deviceAdapter: DeviceAdapter
    private val listDevices = mutableListOf<Device>()
    private val rootController: RootController by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewAll)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Khởi tạo Adapter với listener
        deviceAdapter = DeviceAdapter(::onIsAutoSwitchChanged, ::onValueSwitchChanged)
        recyclerView.adapter = deviceAdapter

        rootController.devices.observe(viewLifecycleOwner) { devices ->
            refreshDataRealTime(devices)
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("MyDataUpdate"))
        fetchAndDisplayDevices()

        return view
    }


    private fun refreshDataRealTime(devices: List<DeviceEntity>) {
        // Cập nhật dữ liệu thời gian thực từ LiveData
        val newDevices = listDevices.map { oldDev ->
            val newDevice = devices.find { it.device_id == oldDev.id }
            oldDev.copy(
                value = newDevice?.value?.toDouble() ?: oldDev.value
            )
        }
        listDevices.clear()
        listDevices.addAll(newDevices)
        deviceAdapter.submitList(newDevices)
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
                            val filteredDevices = room.devices.filter { device ->
                                device.device_type != "temperature" &&
                                        device.device_type != "humidity" &&
                                        device.device_type != "gas"
                            }
                            listDevices.addAll(filteredDevices)
                        }
                        deviceAdapter.submitList(listDevices)
                    }
                } else {
                    println("API call failed: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun onIsAutoSwitchChanged(device: Device, isChecked: Boolean) {
        // Xử lý sự kiện khi Switch isAuto được bật/tắt
        println("isAuto switch clicked for device ${device.name}, isChecked: $isChecked")
        lifecycleScope.launch {
            try {
                val response = ApiClient.api.updateDeviceState(
                    device.id,
                    UpdateDeviceRequest(isChecked, device.value)
                )
                if (response.isSuccessful) {
                    println("Device isAuto state updated successfully")
                } else {
                    println("Failed to update device isAuto state: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun onValueSwitchChanged(device: Device, isChecked: Boolean) {
        // Xử lý sự kiện khi Switch value được bật/tắt
        println("value switch clicked for device ${device.name}, isChecked: $isChecked")
        // Thay đổi giá trị value từ 0 sang 1 và ngược lại
        val newValue = if (isChecked) 1 else 0
        lifecycleScope.launch {
            try {
                val response = ApiClient.api.updateDeviceState(
                    device.id,
                    UpdateDeviceRequest(false, newValue.toDouble())
                )
                if (response.isSuccessful) {
                    println("Device value state updated successfully")
                } else {
                    println("Failed to update device value state: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra("message")?.let { message ->
                rootController.updateDevices(message)
            }
        }
    }
}
