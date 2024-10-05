package com.kma_kit.smarthome.ui.fragment

import PreferencesHelper
import RootController
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessaging
import com.kma_kit.smarthome.R
import com.kma_kit.smarthome.data.model.request.UpdateUser
import com.kma_kit.smarthome.ui.activity.ChangePasswordActivity
import com.kma_kit.smarthome.ui.activity.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SettingsFragment : Fragment() {
    private lateinit var aboutMeButton: CardView
    private lateinit var changeUsernameButton: CardView
    private lateinit var changePasswordButton: CardView
    private lateinit var logoutButton: CardView
    private lateinit var avatar: ImageView
    private lateinit var userName: TextView
    private lateinit var enableNoti: Switch
    private lateinit var enableDarkMode: Switch
    private val rootController: RootController by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_settings, container, false)
        onInit(view)
        listenerEvent()
        return view
    }

    private fun onInit(view: View) {
        aboutMeButton = view.findViewById(R.id.about_me_button)
        changeUsernameButton = view.findViewById(R.id.change_username_button)
        changePasswordButton = view.findViewById(R.id.change_password_button)
        logoutButton = view.findViewById(R.id.logout_button)
        avatar = view.findViewById(R.id.avatar)
        userName = view.findViewById(R.id.username)
        enableNoti = view.findViewById(R.id.switch_notifications)
        enableDarkMode = view.findViewById(R.id.switch_dark_mode)
        rootController.userInfo.observe(viewLifecycleOwner) {
            userName.text = it.first_name + " " + it.last_name
            Log.d("SettingsFragment", "User info: $it")
            enableNoti.isChecked = it.fcm_token.isNotEmpty()

        }
        rootController.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                // Display error message
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        val preferencesHelper = PreferencesHelper.getInstance()
        enableDarkMode.isChecked = preferencesHelper.enableDarkMode
        enableNoti.isChecked = preferencesHelper.enableNotification


        try {
            val inputStream = requireContext().assets.open("img_avatar.png")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            avatar.setImageBitmap(bitmap)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Do nothing
    }


    private fun listenerEvent() {
        enableNoti.setOnCheckedChangeListener { _, isChecked ->
//            lifecycleScope.launch {
//                var fcmToken = ""
//                if (isChecked) {
//                    fcmToken = getToken()
//                }
//
//                val user = rootController.userInfo.value
//                val updatedUser = user?.let {
//                    UpdateUser(
//                        it.first_name,
//                        user.last_name,
//                        user.date_of_birth,
//                        user.gender,
//                        fcmToken
//                    )
//                }
//
//                if (updatedUser != null) {
//                    rootController.updateUserInfo(updatedUser)
//                }
//            }
            val preferencesHelper = PreferencesHelper.getInstance()
            preferencesHelper.enableNotification = isChecked


        }

        enableDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val preferencesHelper = PreferencesHelper.getInstance()
            preferencesHelper.enableDarkMode = isChecked
        }

        aboutMeButton.setOnClickListener {
            var url: String = "https://smarthomekit.vn/"
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://$url"
            }

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }

        changePasswordButton.setOnClickListener {
            val context = requireContext()
            val intent = Intent(context, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                val user = rootController.userInfo.value
                val updatedUser = user?.let {
                    UpdateUser(
                        it.first_name,
                        user.last_name,
                        user.date_of_birth,
                        user.gender,
                        ""
                    )
                }

                if (updatedUser != null) {
                    withContext(Dispatchers.IO) {
                        rootController.updateUserInfo(updatedUser)
                    }
                }
                Log.d("SettingsFragment", "Logout")
                delay(1500)
                Log.d("SettingsFragment", "Logout2")
                val context = requireContext()
                val preferencesHelper = PreferencesHelper.getInstance()
                preferencesHelper.clear()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)



            }






        }
    }

    private suspend fun getToken(): String {
        return suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(Constants.TAG, "Fetching FCM registration token failed", task.exception)
                    continuation.resume("")
                } else {
                    val token = task.result.toString()
                    Log.d(Constants.TAG, "Token_fcm: $token")
                    continuation.resume(token)
                }
            })
        }
    }
}
