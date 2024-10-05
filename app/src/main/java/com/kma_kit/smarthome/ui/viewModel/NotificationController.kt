package com.kma_kit.smarthome.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma_kit.smarthome.data.model.response.Notification
import com.kma_kit.smarthome.repository.UserRepository
import kotlinx.coroutines.launch

class NotificationController : ViewModel() {
    private val _notifications = MutableLiveData<List<Notification>?>()
    val notifications: MutableLiveData<List<Notification>?> get() = _notifications

    private val _error = MutableLiveData<String>()
    val error: MutableLiveData<String> get() = _error

    fun fetchNotifications() {
        viewModelScope.launch {
            try {
                val noti = UserRepository().getNotifications()
                if (noti.isSuccessful) {
                    _notifications.value = noti.body()?.results
                    Log.d("Notification", noti.body()?.results.toString())
                } else {
                    _error.value = "Failed to fetch notifications"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}