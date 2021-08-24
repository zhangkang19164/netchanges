package com.cyyl.netchanges

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import kotlin.properties.Delegates

/**
 * @author : Android-张康
 * created on: 2021/8/24 11:47
 * description: 网络状态变化监听
 */
object Netchanges {

    private var mLastActiveNetworkType: Int by Delegates.notNull()

    internal val networkStatusLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(mLastActiveNetworkType)
    }

    fun init(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager ?: return
        mLastActiveNetworkType = getActiveNetworkType(context)
        val networkCallback = NetworkCallbackImpl()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder().build(),
                networkCallback
            )
        }
    }

    /**
     * 判断当前网络是否连接
     *
     * @param context 上下文
     *
     * @return true 表示有网络 false 表示无网络
     */
    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager ?: return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork =
                connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            val allNetworks = connectivityManager.allNetworks
            if (allNetworks.isEmpty()) {
                return false
            }
            for (network in allNetworks) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                    && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                ) {
                    return true
                }
            }
        }
        return false
    }
    /**
     * 判断当前网络是否连接
     *
     * @param networkType 网络状态
     *
     * @return true 表示有网络 false 表示无网络
     */
    fun isConnected(networkType: Int): Boolean {
        return networkType != NetworkType.NOT_CONNECTED
    }

    /**
     * 获取当前网络连接类型
     *
     * @param context 上下文
     * @return 当前的网络类型
     */
    fun getActiveNetworkType(context: Context): Int {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager ?: return NetworkType.NOT_CONNECTED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork =
                connectivityManager.activeNetwork ?: return NetworkType.NOT_CONNECTED
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                ?: return NetworkType.NOT_CONNECTED
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return NetworkType.WIFI
            }
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return NetworkType.MOBILE
            }
        } else {
            val activeNetworkInfo =
                connectivityManager.activeNetworkInfo ?: return NetworkType.NOT_CONNECTED
            if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.WIFI
            }
            if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                return NetworkType.MOBILE
            }
        }
        return NetworkType.UNKNOWN
    }

    fun registerCallback(
        lifecycleOwner: LifecycleOwner,
        networkChangesListener: NetworkChangesListener
    ) {
        networkStatusLiveData.observe(lifecycleOwner, {
            networkChangesListener.networkChanges(it)
        })
    }


}