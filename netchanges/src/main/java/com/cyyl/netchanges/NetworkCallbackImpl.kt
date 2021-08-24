package com.cyyl.netchanges

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * @author : Android-张康
 * created on: 2021/8/24 15:24
 * description:
 */
const val TAG = "NetworkCallbackImpl"

class NetworkCallbackImpl :
    ConnectivityManager.NetworkCallback() {

    private val mHandler = Handler(Looper.getMainLooper())
    private val mRunnable = Runnable {
        Netchanges.networkStatusLiveData.postValue(NetworkType.NOT_CONNECTED)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Log.i(TAG, "默认网络现在是: $network")
        mHandler.removeCallbacks(mRunnable)
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
        super.onLosing(network, maxMsToLive)
        Log.i(TAG, "该应用程序即将丢失默认网络。最后一个默认网络是 $network maxMsToLive = $maxMsToLive")
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        Log.i(
            TAG,
            "该应用程序不再具有默认网络。最后一个默认网络是 $network"
        )
        mHandler.postDelayed(mRunnable, 500)
    }


    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        Log.i(TAG, "默认网络更改功能: $networkCapabilities")
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
        ) {
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    postValue(NetworkType.WIFI)
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    postValue(NetworkType.MOBILE)
                }
                else -> {
                    postValue(NetworkType.UNKNOWN)
                }
            }
        }
    }

    private fun postValue(networkType: Int) {
        if (Netchanges.networkStatusLiveData.value != networkType) {
            Netchanges.networkStatusLiveData.postValue(networkType)
        }

    }
}