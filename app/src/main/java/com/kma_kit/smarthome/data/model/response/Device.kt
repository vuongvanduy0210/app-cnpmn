package com.kma_kit.smarthome.data.model.response

data class Device(
    val id: String,
    val name: String,
    var is_auto: Boolean,
    val device_type: String,
    val auto_available: Boolean,
    var value: Double
) {
    val typeName: String
        get() = when (device_type) {
            "bulb" -> "Bóng đèn"
            "water" -> "Tưới nươớc"
            "fan" -> "Quạt"
            "humidity" -> "Độ ẩm"
            "temperature" -> "Nhiệt độ"
            else -> "Không xác định"
        }
}