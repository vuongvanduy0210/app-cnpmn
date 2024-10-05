package com.kma_kit.smarthome.data.model.request

data class ChangePassword(
    val old_password: String,
    val new_password: String,
)