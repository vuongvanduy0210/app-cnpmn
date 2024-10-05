package com.kma_kit.smarthome.ui.fragment

import RootController
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.kma_kit.smarthome.R
import com.kma_kit.smarthome.data.model.response.HomeResponse
import com.kma_kit.smarthome.ui.activity.NotificationsActivity
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val GAS_MIN_VALUE = 40
const val TEMP_MIN_VALUE = 35

class HomeFragment : Fragment() {
    private lateinit var notificationIcon: ImageView
    private val rootController: RootController by activityViewModels()
    private lateinit var temperatureTextView: TextView
    private lateinit var gasTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var lightTextView: TextView
    private var numberOfLightsWithValue1: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        temperatureTextView = view.findViewById(R.id.temperatureTextView)
        gasTextView = view.findViewById(R.id.gasTextView)
        humidityTextView = view.findViewById(R.id.humidityTextView)
        lightTextView = view.findViewById(R.id.lightTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        dateTextView.text = currentDate
    initData()
        rootController.devices.observe(viewLifecycleOwner) { devices ->
            var totalLightsOn = 0
            devices.forEach { deviceEntity ->
                when (deviceEntity.type) {
                    "humidity" -> humidityTextView.text = deviceEntity.value.toString() + "%"
                    "temperature" -> {
                        temperatureTextView.text = deviceEntity.value.toString() + "°C"
                        if (deviceEntity.value > TEMP_MIN_VALUE) {
                            temperatureTextView.setTextColor(resources.getColor(R.color.red))
                        } else {
                            temperatureTextView.setTextColor(resources.getColor(R.color.textColor))
                        }
                    }

                    "gas" -> {
                        gasTextView.text = deviceEntity.value.toString() + "%"
                        if (deviceEntity.value > GAS_MIN_VALUE) {
                            gasTextView.setTextColor(resources.getColor(R.color.red))
                        } else {
                            gasTextView.setTextColor(resources.getColor(R.color.textColor))
                        }
                    }

                    "bulb" -> {
                        if (deviceEntity.value == 1) {
                            totalLightsOn++
                        }
                        // Update other UI elements related to bulb if needed
                    }
                }
            }
            lightTextView.text = totalLightsOn.toString()
        }

        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        notificationIcon = view.findViewById(R.id.notificationIcon)

        listenEvent()

        // Set initial fragment
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                replace(R.id.fragmentContainer, AllFragment())
            }
        }

        // Set up tab selected listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedFragment = when (tab.position) {
                    0 -> AllFragment()
                    1 -> LivingRoomFragment()
                    2 -> KitchenFragment()
                    3 -> BedroomFragment()
                    else -> AllFragment()
                }
                childFragmentManager.commit {
                    replace(R.id.fragmentContainer, selectedFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Do nothing
            }
        })

        return view
    }

    private fun initData() {
        lifecycleScope.launch {
            try {
                val response: Response<HomeResponse> = ApiClient.api.getDevices()

                if (response.isSuccessful) {
                    val homeResponse = response.body()
                    homeResponse?.let { home ->
                        var totalLightsOn = 0

                        home.rooms.forEach { room ->
                            val filteredDevices = room.devices.filter { device ->
                                when (device.device_type) {
                                    "temperature" -> {
                                        temperatureTextView.text = "${device.value.toInt()}°C"
                                        false
                                    }
                                    "humidity" -> {
                                        humidityTextView.text = "${device.value.toInt()}%"
                                        false
                                    }
                                    "gas" -> {
                                        gasTextView.text = "${device.value.toInt()}%"
                                        false
                                    }
                                    "bulb" -> {
                                        if (device.value == 1.0) {
                                            totalLightsOn++
                                        }
                                        true
                                    }
                                    else -> true
                                }
                            }
                        }
                        lightTextView.text = " $totalLightsOn"
                    }
                } else {
                    println("API call failed: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun listenEvent() {
        notificationIcon.setOnClickListener {
            var intent = Intent(context, NotificationsActivity::class.java)
            startActivity(intent)
        }
    }
    private fun updateNumberOfLightsTextView(number: String) {
        // Cập nhật số lượng đèn có value = 1 vào TextView
        lightTextView.text = numberOfLightsWithValue1.toString()
    }
}
