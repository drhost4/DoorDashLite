package com.example.doordashlite.utilties

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectivityUtility {
    companion object {
        fun isConnected(connMgr: ConnectivityManager): Boolean {
            val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
            return networkInfo?.isConnected == true
        }
    }
}