package com.gennakersystems.cloudping

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import java.net.Inet4Address

fun getGatewayIp(context: Context): String {
    return try {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return "N/A"
        val linkProperties = connectivityManager.getLinkProperties(network) ?: return "N/A"

        // Search for any route that has a gateway, not just the strict "default" route
        // This is more resilient on tethered or VPN networks
        val gateway = linkProperties.routes.firstOrNull { it.gateway is Inet4Address }?.gateway
        gateway?.hostAddress ?: "N/A"
    } catch (e: Exception) {
        Log.e("NetworkUtils", "Error finding gateway", e)
        "N/A"
    }
}

fun getInternalIp(context: Context): String {
    return try {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return "N/A"
        val linkProperties = connectivityManager.getLinkProperties(network) ?: return "N/A"
        
        for (linkAddress in linkProperties.linkAddresses) {
            val address = linkAddress.address
            if (address is Inet4Address && !address.isLoopbackAddress) {
                return address.hostAddress ?: "N/A"
            }
        }
        "N/A"
    } catch (e: Exception) {
        Log.e("NetworkUtils", "Error finding internal IP", e)
        "N/A"
    }
}
