package com.example.sampahku

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * Broadcast Receiver untuk menangani berbagai event sistem Android.
 * Mengimplementasikan deteksi baterai, pengisian daya, mode pesawat, dan koneksi internet.
 */
class SystemReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // [INDIKATOR] Fungsi ini akan terpanggil otomatis saat sistem Android mengirimkan sinyal/broadcast
        
        // Inisialisasi channel notifikasi jika belum ada
        NotificationHelper.createNotificationChannel(context)

        when (intent.action) {
            // [IMPLEMENTASI IDE 2] Mendeteksi saat baterai HP berada di level rendah (biasanya < 15%)
            Intent.ACTION_BATTERY_LOW -> {
                NotificationHelper.sendNotification(
                    context,
                    context.getString(R.string.notif_battery_title),
                    context.getString(R.string.notif_battery_msg),
                    101
                )
            }

            // [IMPLEMENTASI IDE 4] Mendeteksi saat HP mulai dihubungkan ke pengisi daya (charger)
            Intent.ACTION_POWER_CONNECTED -> {
                NotificationHelper.sendNotification(
                    context,
                    context.getString(R.string.notif_power_title),
                    context.getString(R.string.notif_power_msg),
                    102
                )
            }

            // [IMPLEMENTASI IDE 6] Mendeteksi saat mode pesawat dinyalakan atau dimatikan
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                NotificationHelper.sendNotification(
                    context,
                    context.getString(R.string.notif_airplane_title),
                    context.getString(R.string.notif_airplane_msg),
                    103
                )
            }

            // [IMPLEMENTASI IDE 3] Mendeteksi perubahan koneksi internet (WiFi atau Data Seluler)
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                if (isOnline(context)) {
                    // Muncul jika internet kembali tersambung
                    NotificationHelper.sendNotification(
                        context,
                        context.getString(R.string.notif_network_online_title),
                        context.getString(R.string.notif_network_online_msg),
                        104
                    )
                } else {
                    // Muncul jika internet terputus
                    NotificationHelper.sendNotification(
                        context,
                        context.getString(R.string.notif_network_offline_title),
                        context.getString(R.string.notif_network_offline_msg),
                        104
                    )
                }
            }
        }
    }

    /**
     * Fungsi pembantu untuk mengecek apakah perangkat sedang online
     */
    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo != null && networkInfo.isConnected
        }
    }
}