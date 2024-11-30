package com.unlam.tpmarvel.data

interface NetworkConnectivityChecker {
    fun isNetworkAvailable(): Boolean
}