package com.kma_kit.smarthome.data.entity

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class DeviceEntity(
    val device_id: String,
    val type: String,
    val value: Int,
    val is_auto: Boolean,
    val auto_available: Boolean
)
fun parseJson(jsonString: String): List<DeviceEntity> {
    val gson = Gson()
    val listDeviceType = object : TypeToken<List<DeviceEntity>>() {}.type
    return gson.fromJson(jsonString, listDeviceType)
}

