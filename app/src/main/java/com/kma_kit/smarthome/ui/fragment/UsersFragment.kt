// UserInfoFragment.kt
package com.kma_kit.smarthome.ui.fragment

import RootController
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.kma_kit.smarthome.R
import java.io.IOException


class UsersFragment : Fragment() {

    private lateinit var avatarImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var birthDateTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var ageTextView: TextView
    private val rootController: RootController by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_users, container, false)

        avatarImageView = view.findViewById(R.id.avatar)
        usernameTextView = view.findViewById(R.id.username)
        emailTextView = view.findViewById(R.id.email)
        birthDateTextView = view.findViewById(R.id.dob)
        genderTextView = view.findViewById(R.id.gender)
        ageTextView = view.findViewById(R.id.age)

        // Set user information (for demonstration purposes, replace with actual data)
        try {
            val inputStream = requireContext().assets.open("img_avatar.png")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            avatarImageView.setImageBitmap(bitmap)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        rootController.userInfo.observe(viewLifecycleOwner) {
            usernameTextView.text = it.first_name + " " + it.last_name
            birthDateTextView.text = it.date_of_birth
            ageTextView.text = "18"
            if (it.gender) {
                genderTextView.text = "Male"
            } else genderTextView.text = "Female"
            emailTextView.text = it.email
        }
        rootController.devices.observe(viewLifecycleOwner, Observer { devices ->
            devices.forEach { deviceEntity ->
                if (deviceEntity.type == "humidity") {
                    Log.d("humidity", deviceEntity.toString())
                }
            }
        })


        rootController.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                // Display error message
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        return view
    }

}
