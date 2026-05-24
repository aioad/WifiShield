package com.example.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val securityChannel = NotificationChannel(
                SECURITY_CHANNEL_ID,
                "Cyber Security Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Warns of unencrypted Wi-Fi hazards or threat indicators."
            }

            val vpnChannel = NotificationChannel(
                VPN_CHANNEL_ID,
                "Shield VPN Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Monitors active in-app encryption tunnel stages."
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(securityChannel)
            manager.createNotificationChannel(vpnChannel)
        }
    }

    fun showUnsafeWifiNotification(ssid: String, score: Int) {
        val intent = Intent(context, com.example.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, SECURITY_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🚨 Threat Alert: Unsafe Wi-Fi")
            .setContentText("Connected to '$ssid' (Score: $score/100). Personal data may be vulnerable. Protection recommended.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(SECURITY_ALERT_ID, notification)
    }

    fun showVpnEnabledNotification() {
        val notification = NotificationCompat.Builder(context, VPN_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("🛡️ WiFi Shield VPN Engaged")
            .setContentText("Full-spectrum in-app data encryption active. Your browsing is secure.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(VPN_STATUS_ALERT_ID, notification)
    }

    fun showSafeNetworkConnectedNotification(ssid: String) {
        val notification = NotificationCompat.Builder(context, SECURITY_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🟢 Secure Network Verified")
            .setContentText("Connected to '$ssid'. High cybersecurity reputation score. Safe to browse.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(SAFE_CONNECTED_ID, notification)
    }

    companion object {
        private const val SECURITY_CHANNEL_ID = "security_channel"
        private const val VPN_CHANNEL_ID = "vpn_channel"

        private const val SECURITY_ALERT_ID = 2021
        private const val VPN_STATUS_ALERT_ID = 2022
        private const val SAFE_CONNECTED_ID = 2023
    }
}
