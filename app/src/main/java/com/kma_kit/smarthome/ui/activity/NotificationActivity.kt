package com.kma_kit.smarthome.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kma_kit.smarthome.R
import com.kma_kit.smarthome.ui.adapter.NotificationAdapter
import com.kma_kit.smarthome.ui.viewModel.NotificationController

class NotificationsActivity : AppCompatActivity() {
    private lateinit var viewModel: NotificationController
    private lateinit var adapter: NotificationAdapter
    private lateinit var backIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        viewModel = ViewModelProvider(this).get(NotificationController::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        backIcon = findViewById(R.id.back_icon)
        backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        viewModel.fetchNotifications()

        viewModel.notifications.observe(this, Observer { notifications ->
            adapter = NotificationAdapter(notifications!!)
            recyclerView.adapter = adapter
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
