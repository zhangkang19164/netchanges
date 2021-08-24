package com.cyyl.netchanges

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Netchanges.init(this)
        Netchanges.registerCallback(this, object : NetworkChangesListener {
            /**
             * 网络变化回调
             */
            override fun networkChanges(networkType: Int) {
                Log.i(TAG, "networkChanges: networkType = $networkType")
            }

        })


    }

    override fun onResume() {
        super.onResume()

    }

    fun getNetworkStatus(view: View) {
        val textView = findViewById<TextView>(R.id.text_view)
//        if (Netchanges.isNetworkConnect(this)) {
//            textView.text = "当前有网络连接"
//        } else {
//            textView.text = "当前无网络连接"
//        }


        when (Netchanges.getActiveNetworkType(this)) {
            NetworkType.NOT_CONNECTED -> {
                textView.text = "当前无网络连接"
            }
            NetworkType.WIFI -> {
                textView.text = "当前网络连接WIFI"
            }
            NetworkType.MOBILE -> {
                textView.text = "当前连接手机网络"
            }
        }
    }
}