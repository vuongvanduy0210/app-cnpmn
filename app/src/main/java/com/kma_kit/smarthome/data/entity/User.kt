package com.kma_kit.smarthome.data.entity

data class UserEntity(
    val id: Long,
    var name: String,
    var email: String,
    var dateOfBirth: String
) {
    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updateName(newName: String) {
        name = newName
    }
}