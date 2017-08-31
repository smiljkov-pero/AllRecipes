package de.foodora.android.networkutils

import de.foodora.android.data.models.networkutils.NetworkStatusSnapshot

interface NetworkTypeDetector {
    companion object {
        val NETWORK_TYPE_2G = "2G"
        val NETWORK_TYPE_3G = "3G"
        val NETWORK_TYPE_4G = "4G"
        val NETWORK_TYPE_WIFI = "WIFI"
        val NETWORK_TYPE_UNKNOWN = "Unknown"
        val NETWORK_TYPE_NONE = "None"
    }
    fun createNetworkStatusSnapshot(): NetworkStatusSnapshot
}
