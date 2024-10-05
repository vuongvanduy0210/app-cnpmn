package com.kma_kit.smarthome.data.model.response

data class HomeResponse(
    val id: String,
    val address: String,
    val rooms: List<Room>
)
