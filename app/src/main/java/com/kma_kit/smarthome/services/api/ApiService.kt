package com.kma_kit.smarthome.services.api

import UpdateDeviceRequest
import com.kma_kit.smarthome.data.model.request.ChangePassword
import com.kma_kit.smarthome.data.model.request.UpdateUser
import com.kma_kit.smarthome.data.model.request.UserAuth
import com.kma_kit.smarthome.data.model.response.AuthResponse
import com.kma_kit.smarthome.data.model.response.HomeResponse
import com.kma_kit.smarthome.data.model.response.NotificationResponse
import com.kma_kit.smarthome.data.model.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("user/me/")
    suspend fun getUser(): Response<UserResponse>
    @POST("user/auth/")
    suspend fun loginUser(@Body userAuth: UserAuth): Response<AuthResponse>

    @PUT("user/me/")
    suspend fun updateUser(@Body user: UserResponse): Response<UserResponse>

    @POST("user/change-password/")
    suspend fun changePassword(@Body changePassword: ChangePassword) : Response<Void>

    @PATCH("user/update-details/")
    suspend fun updateDetails(@Body user: UpdateUser): Response<UserResponse>

    @GET("home/my")
    suspend fun getDevices(): Response<HomeResponse>

    @PATCH("/api/home/device/{id}/update/")
    suspend fun updateDeviceState(
        @Path("id") id: String,
        @Body request: UpdateDeviceRequest
    ): Response<Void>

     @GET("notifications/list/?ordering=0&page=1&page_size=100")
     suspend fun getNotifications(): Response<NotificationResponse>
}