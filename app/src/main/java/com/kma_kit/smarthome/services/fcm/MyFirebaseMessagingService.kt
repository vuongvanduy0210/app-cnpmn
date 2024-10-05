package com.kma_kit.smarthome.services.fcm

import PreferencesHelper
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kma_kit.smarthome.R
import com.kma_kit.smarthome.ui.SmartHomeApplication
import com.kma_kit.smarthome.ui.activity.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            handleDataPayload(remoteMessage.data)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            if (it.body != null && it.body != "update") {
                val preferencesHelper = PreferencesHelper.getInstance()
                if (preferencesHelper.enableNotification)
                {
                    showNotification(it.title ?: "FCM Message", it.body ?: "")
                }

            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Xử lý khi có sự thay đổi token FCM của thiết bị
        Log.d(TAG, "Refreshed token: $token")
        // Lưu token này và gửi đến server nếu cần
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun showNotification(title: String, body: String) {
        val intent = Intent(SmartHomeApplication.getAppContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            SmartHomeApplication.getAppContext(), 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(SmartHomeApplication.getAppContext(), channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    private fun handleDataPayload(data: Map<String, String>) {
        val jsonString = data["data"]
        jsonString?.let {
            sendMessageToViewModel(it)
        }
    }

    private fun sendMessageToViewModel(message: String) {
        val intent = Intent("MyDataUpdate")
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }
}
