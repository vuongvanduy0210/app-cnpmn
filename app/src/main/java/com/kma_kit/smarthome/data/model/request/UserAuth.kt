package com.kma_kit.smarthome.data.model.request

data class UserAuth (
    val email: String,
    val password: String,
    val fcm_token: String,
)