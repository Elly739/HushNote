package com.example.hushnote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast

class ConnectivityReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "ConnectivityReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Broadcast received: ${intent.action}")

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Toast.makeText(context, "HushNote: System booted. Notes ready.", Toast.LENGTH_LONG).show()
            }
            else -> {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                val network = cm?.activeNetwork
                val capabilities = cm?.getNetworkCapabilities(network)
                val isConnected = capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )

                val statusText = if (isConnected) "Online - Daily Quotes Enabled" else "Offline - Cached Mode"
                Log.d(TAG, "Network state: $statusText")
            }
        }
    }
}
