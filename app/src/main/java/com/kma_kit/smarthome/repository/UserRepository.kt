package com.kma_kit.smarthome.repository

import ApiClient
import com.kma_kit.smarthome.data.model.request.ChangePassword
import com.kma_kit.smarthome.data.model.request.UpdateUser
import com.kma_kit.smarthome.data.model.request.UserAuth
import com.kma_kit.smarthome.data.model.response.AuthResponse
import com.kma_kit.smarthome.data.model.response.HomeResponse
import com.kma_kit.smarthome.data.model.response.NotificationResponse
import com.kma_kit.smarthome.data.model.response.UserResponse
import retrofit2.Response

class UserRepository {


    suspend fun loginUser(userAuth: UserAuth): Response<AuthResponse> {
        return ApiClient.api.loginUser(userAuth)
    }

    suspend fun getUser(): Response<UserResponse> {
        return ApiClient.api.getUser()
    }

    suspend fun changePassword(changePassword: ChangePassword): Response<Void> {
        return ApiClient.api.changePassword(changePassword)
    }

    suspend fun updateUser(user: UpdateUser): Response<UserResponse> {
        return ApiClient.api.updateDetails(user)
    }

    suspend fun getDevice():Response<HomeResponse>{
        return  ApiClient.api.getDevices()
    }
    suspend fun getNotifications(): Response<NotificationResponse> {
        return ApiClient.api.getNotifications()
    }
}