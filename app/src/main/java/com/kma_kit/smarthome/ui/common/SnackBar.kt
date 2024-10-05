package com.kma_kit.smarthome.ui.common

import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject

fun showSnackbar(view: View, message: String) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackbar.show()
}

fun handleApiError(errorBody: ResponseBody?, view: View) {
    errorBody?.let {
        try {
            val json = JSONObject(errorBody.string())
            val type = json.getString("type")
            val errorsArray = json.getJSONArray("errors")

            val error = errorsArray.getJSONObject(0)
            val errorCode = error.getString("code")
            val errorDetail = error.getString("detail")


            Log.d("LoginActivity", "Validation error: $errorCode - $errorDetail")
            showSnackbar(view, "Validation error: $errorDetail")

        } catch (e: JSONException) {
            Log.e("LoginActivity", "Error parsing JSON", e)
            showSnackbar(view, "Error parsing server response.")
        }
    }
}
