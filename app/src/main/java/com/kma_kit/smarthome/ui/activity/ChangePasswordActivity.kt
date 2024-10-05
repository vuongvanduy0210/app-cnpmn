package com.kma_kit.smarthome.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.kma_kit.smarthome.R
import com.kma_kit.smarthome.data.model.request.ChangePassword
import com.kma_kit.smarthome.repository.UserRepository
import com.kma_kit.smarthome.ui.common.handleApiError
import com.kma_kit.smarthome.ui.common.showSnackbar
import kotlinx.coroutines.launch
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        onInit()
        listenEvent()

    }

    private fun onInit() {
        oldPassword = findViewById(R.id.text_input_old_password)
        newPassword = findViewById(R.id.text_input_new_password)
        confirmPassword = findViewById(R.id.text_input_confirm_password)
    }

    private fun listenEvent() {
        findViewById<Button>(R.id.btn_change_password).setOnClickListener {
            if (validateInput()) {
                val changePassword = ChangePassword(
                    oldPassword.text.toString(),
                    newPassword.text.toString()
                )

                lifecycleScope.launch {
                    var response: Response<Void>
                    val view = findViewById<View>(android.R.id.content)
                    try {
                        response = UserRepository().changePassword(changePassword)
                        if (response.isSuccessful) {
                            showSnackbar(view, "Password changed successfully")

                            val preferencesHelper = PreferencesHelper.getInstance()
                            preferencesHelper.clear()
                            val intent =
                                Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish() // Đóng activity hiện tại
                        }
                    } catch (e: Exception) {
                        // Xử lý lỗi nếu có

                    }
                }

            }
        }
    }

    private fun validateInput(): Boolean {
        if (oldPassword.text.toString().isEmpty()) {
            oldPassword.error = "Old password is required"
            return false
        }
        if (newPassword.text.toString().isEmpty()) {
            newPassword.error = "New password is required"
            return false
        }
        if (confirmPassword.text.toString().isEmpty()) {
            confirmPassword.error = "Confirm password is required"
            return false
        }
        if (newPassword.text.toString() != confirmPassword.text.toString()) {
            confirmPassword.error = "Password does not match"
            return false
        }
        return true
    }
}