package com.gennakersystems.cloudping

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.Inet4Address

fun getGatewayIp(context: Context): String {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return "N/A"
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return "N/A"

    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
        val dhcpInfo = wifiManager.dhcpInfo
        val gatewayIp = dhcpInfo.gateway
        return (gatewayIp and 0xFF).toString() + "." + (gatewayIp shr 8 and 0xFF) + "." + (gatewayIp shr 16 and 0xFF) + "." + (gatewayIp shr 24 and 0xFF)
    }

    return "N/A"
}

fun getInternalIp(context: Context): String {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return "N/A"
    val linkProperties = connectivityManager.getLinkProperties(network) ?: return "N/A"
    for (linkAddress in linkProperties.linkAddresses) {
        val address = linkAddress.address
        if (address is Inet4Address && !address.isLoopbackAddress) {
            return address.hostAddress
        }
    }
    return "N/A"
}
