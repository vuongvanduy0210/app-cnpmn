package com.kma_kit.smarthome.data.model.response

data class Room(
    val id: String,
    val name: String,
    val devices: List<Device>
)