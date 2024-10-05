package com.kma_kit.smarthome.data.model.request

data class UpdateUser (
    val first_name: String,
    val last_name: String,
    val date_of_birth: String,
    val gender : Boolean,
    val fcm_token : String
)