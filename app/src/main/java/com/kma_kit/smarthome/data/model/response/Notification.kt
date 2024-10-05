package com.kma_kit.smarthome.data.model.response

data class Notification(
    val title: String,
    val body: String,
    val created_at: String
)

data class NotificationResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val current: Int,
    val pagesCount: Int,
    val pageSize: Int,
    val results: List<Notification>
)
