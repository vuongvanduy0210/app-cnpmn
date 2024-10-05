package com.kma_kit.smarthome.ui.activity

import PreferencesHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.kma_kit.smarthome.R
import com.kma_kit.smarthome.data.model.request.UserAuth
import com.kma_kit.smarthome.repository.UserRepository
import com.kma_kit.smarthome.ui.common.handleApiError
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginActivity : AppCompatActivity() {
    lateinit var edtUserName: EditText
    lateinit var edtPassword: EditText
    lateinit var btnLogin: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        edtUserName = findViewById(R.id.username)
        edtPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.loginButton)
        configureListeners()
    }

    private fun configureListeners() {
        btnLogin.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked")
            lifecycleScope.launch {
                login()
            }
        }
    }

    private suspend fun login() {
        var token = getToken()
        val username = edtUserName.text.toString()
        val password = edtPassword.text.toString()
        val userAuth = UserAuth(username, password, token)
        val response = UserRepository().loginUser(userAuth)
        if (response.isSuccessful) {
            val authResponse = response.body()
            if (authResponse != null) {
                Log.d("LoginActivity", "Login successful")
                val intent = Intent(this, HomeScreenActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val preferencesHelper = PreferencesHelper.getInstance()
                preferencesHelper.authToken = authResponse.access

                startActivity(intent)
            }
        } else {
            Log.d("LoginActivity", "Login failed")
            val view = findViewById<View>(android.R.id.content)
            handleApiError(response.errorBody(), view)
        }
    }

    private fun checkValidate(){
        if (edtUserName.text.toString().isEmpty()){
            edtUserName.error = "Please enter username"
            return
        }
        if(edtUserName.text.toString().length < 6){
            edtUserName.error = "Username must be at least 6 characters"
            return
        }
        if (edtPassword.text.toString().isEmpty()){
            edtPassword.error = "Please enter password"
            return
        }
        if(edtPassword.text.toString().length < 6){
            edtPassword.error = "Password must be at least 6 characters"
            return
        }
    }

    private suspend fun getToken(): String {
        return suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    continuation.resume("")
                } else {
                    val token = task.result.toString()
                    Log.d(TAG, "Token_fcm: $token")
                    continuation.resume(token)
                }
            })
        }
    }
}