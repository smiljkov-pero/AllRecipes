package de.foodora.android.data.models.networkutils

class NetworkStatusSnapshot {
    var status: String? = null
    var isConnected: Boolean = false
    var hasInternetConnectivity: Boolean = false
    var networkConnectionType: String? = null
    var networkOperator: String? = null
}
