package com.kma_kit.smarthome.data.model.response

import android.os.Parcelable

data class UserResponse(
    var email: String,
    var first_name: String,
    var last_name: String,
    var date_of_birth: String,
    var gender: Boolean,
    var id : String,
    var fcm_token : String
)