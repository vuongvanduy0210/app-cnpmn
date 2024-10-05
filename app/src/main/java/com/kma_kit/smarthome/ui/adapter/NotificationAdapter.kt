package com.kma_kit.smarthome.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.kma_kit.smarthome.R
import com.kma_kit.smarthome.data.model.response.Notification
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class NotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.notificationTitle)
        val body: TextView = itemView.findViewById(R.id.notificationBody)
        val createdAt: TextView = itemView.findViewById(R.id.notificationCreatedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.title.text = notification.title
        holder.body.text = notification.body

        val inputDateStr = notification.created_at
        val instant = Instant.parse(inputDateStr)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy", Locale.getDefault())

        val formattedDateTime = dateTime.format(outputFormatter)
        holder.createdAt.text = formattedDateTime
    }

    override fun getItemCount() = notifications.size
}
