import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kma_kit.smarthome.data.entity.DeviceEntity
import com.kma_kit.smarthome.data.model.request.UpdateUser
import com.kma_kit.smarthome.data.model.response.HomeResponse
import com.kma_kit.smarthome.data.model.response.UserResponse
import com.kma_kit.smarthome.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class RootController : ViewModel() {
    private val _userInfo = MutableLiveData<UserResponse>()
    val userInfo: LiveData<UserResponse> get() = _userInfo

    private val _devices = MutableLiveData<List<DeviceEntity>>()
    val devices: LiveData<List<DeviceEntity>> get() = _devices

    private val _numberOfLightsOn = MutableLiveData<Int>()
    val numberOfLightsOn: LiveData<Int> get() = _numberOfLightsOn

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val fetchedUserInfo = apiCallToFetchUserInfo()
                _userInfo.value = fetchedUserInfo
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateUserInfo(user: UpdateUser) {
        viewModelScope.launch {
            try {
                val updatedUserInfo = UserRepository().updateUser(user)
                if (updatedUserInfo.isSuccessful) {
                    _userInfo.value = updatedUserInfo.body()
                } else {
                    _error.value = "Failed to update user info"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private suspend fun apiCallToFetchUserInfo(): UserResponse {
        try {
            val response = UserRepository().getUser()
            if (response.isSuccessful) {
                return response.body()!!
            } else {
                throw Exception("Failed to fetch user info")
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch user info")
        }
    }

    fun updateDevices(data: String) {
        Log.d("RootController", "updateDevices called with data: $data")
        try {
            val devices = parseData(data)
            _devices.postValue(devices)

            // Calculate number of lights with value 1
            var totalLightsOn = 0
            devices.forEach { device ->
                if (device.type == "bulb" && device.value == 1) {
                    totalLightsOn++
                }
            }
            _numberOfLightsOn.postValue(totalLightsOn)

        } catch (e: Exception) {
        }
    }

    private fun parseData(data: String): List<DeviceEntity> {
        val gson = Gson()
        val deviceListType = object : TypeToken<List<DeviceEntity>>() {}.type
        val devices: List<DeviceEntity> = gson.fromJson(data, deviceListType)
        return devices.map { device ->
            DeviceEntity(device.device_id, device.type, device.value, is_auto = device.is_auto, auto_available = device.auto_available)
        }
    }
}
