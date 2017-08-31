package de.foodora.android.networkutils

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import de.foodora.android.data.models.networkutils.NetworkStatusSnapshot

class DeviceNetworkTypeTypeDetector(
    private val connectivityManager: ConnectivityManager,
    private val telephonyManager: TelephonyManager
) : NetworkTypeDetector {

    override fun createNetworkStatusSnapshot(): NetworkStatusSnapshot {
        val result = NetworkStatusSnapshot()
        val activeNetwork = connectivityManager.activeNetworkInfo

        result.hasInternetConnectivity = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        result.isConnected = checkIsConnected(activeNetwork)
        result.status = checkConnectionStatus(activeNetwork)
        result.networkConnectionType = if (result.isConnected) getNetworkType() else NetworkTypeDetector.NETWORK_TYPE_NONE
        result.networkOperator = telephonyManager.networkOperatorName

        return result
    }

    private fun checkIsConnected(activeNetwork: NetworkInfo): Boolean {
        return activeNetwork != null
            && activeNetwork.isAvailable
            && activeNetwork.detailedState == NetworkInfo.DetailedState.CONNECTED
    }

    private fun checkConnectionStatus(activeNetwork: NetworkInfo): String {
        return if (activeNetwork != null && activeNetwork.isAvailable
            && activeNetwork.isConnectedOrConnecting) "enabled" else "disabled"
    }

    private fun getNetworkType(): String {
        val activeNetwork = connectivityManager.activeNetworkInfo
        var networkType = NetworkTypeDetector.NETWORK_TYPE_UNKNOWN

        if (activeNetwork != null) {
            when (activeNetwork.type) {
                ConnectivityManager.TYPE_WIFI -> networkType = NetworkTypeDetector.NETWORK_TYPE_WIFI
                ConnectivityManager.TYPE_MOBILE -> networkType = getMobileNetworkType()
                else -> NetworkTypeDetector.NETWORK_TYPE_UNKNOWN
            }
        }

        return networkType
    }

    private fun getMobileNetworkType(): String {
        val networkType = telephonyManager.networkType
        when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN
                -> return NetworkTypeDetector.NETWORK_TYPE_2G
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP
                -> return NetworkTypeDetector.NETWORK_TYPE_3G
            TelephonyManager.NETWORK_TYPE_LTE
                -> return NetworkTypeDetector.NETWORK_TYPE_4G
            else
                -> return NetworkTypeDetector.NETWORK_TYPE_UNKNOWN
        }
    }
}
