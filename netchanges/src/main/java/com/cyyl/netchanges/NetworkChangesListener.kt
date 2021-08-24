package com.cyyl.netchanges

/**
 * @author : Android-张康
 * created on: 2021/8/24 11:56
 * description:网络变化监听
 */
interface NetworkChangesListener {
    /**
     * 网络变化回调
     */
    fun networkChanges(networkType: Int)
}