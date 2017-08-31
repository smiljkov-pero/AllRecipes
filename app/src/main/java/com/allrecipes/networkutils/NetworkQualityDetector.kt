package de.foodora.android.networkutils

import de.foodora.android.data.models.networkutils.NetworkStatusSnapshot

class NetworkQualityDetector(private val networkTypeDetector: NetworkTypeDetector) : NetworkQuality {

    override fun getNetworkQualityCoefficient(): Double {
        val networkSnapshot: NetworkStatusSnapshot = networkTypeDetector.createNetworkStatusSnapshot()

        return when (networkSnapshot.networkConnectionType) {
            NetworkTypeDetector.NETWORK_TYPE_2G,
            NetworkTypeDetector.NETWORK_TYPE_3G -> 0.5
            NetworkTypeDetector.NETWORK_TYPE_4G,
            NetworkTypeDetector.NETWORK_TYPE_WIFI -> 1.0
            else -> 0.5
        }
    }
}
