package com.example.sampahku

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Helper class untuk mengelola notifikasi di aplikasi SampahKu.
 * Dibuat untuk memudahkan pengiriman notifikasi dari Broadcast Receiver.
 */
object NotificationHelper {
    private const val CHANNEL_ID = "sampahku_notifications"
    private const val NOTIFICATION_ID_BASE = 100

    /**
     * Membuat Notification Channel (Wajib untuk Android 8.0 Oreo ke atas)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notif_channel_name)
            val descriptionText = "Channel untuk notifikasi sistem SampahKu"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Mengirimkan notifikasi sederhana ke pengguna
     */
    fun sendNotification(context: Context, title: String, message: String, notificationId: Int = NOTIFICATION_ID_BASE) {
        // Cek izin notifikasi untuk Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Jika tidak ada izin, jangan kirim notifikasi (atau log error)
                return
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_gift) // Menggunakan ikon gift yang sudah ada
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}