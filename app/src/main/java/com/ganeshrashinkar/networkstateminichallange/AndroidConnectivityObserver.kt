package com.ganeshrashinkar.networkstateminichallange

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidConnectivityObserver(
    private val context: Context,
): ConnectivityObserver {

    private val connectivityManager=context.getSystemService<ConnectivityManager>()!!

    override var isAirplaneMode:Flow<Boolean> = callbackFlow {
        val receiver=object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                val isOn=p1?.getBooleanExtra("state",false)?:false
                trySend(isOn)
            }
        }
        val filter= IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
    override val isConnected: Flow<Boolean>
        get() = callbackFlow {
            val callBack=object : ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                        trySend(true)

                }

                override fun onUnavailable() {
                    super.onUnavailable()
                        trySend(false)

                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                        trySend(false)
                }

                override fun onBlockedStatusChanged(
                    network: Network,
                    blocked: Boolean
                ) {
                    super.onBlockedStatusChanged(network, blocked)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val connected=networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )

                        trySend(connected)
                }

            }
            connectivityManager.registerDefaultNetworkCallback(callBack)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callBack)
            }
        }
}