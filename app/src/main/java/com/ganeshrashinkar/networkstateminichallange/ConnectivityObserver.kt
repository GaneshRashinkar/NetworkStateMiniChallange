package com.ganeshrashinkar.networkstateminichallange

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isConnected: Flow<Boolean>

    var isAirplaneMode: Flow<Boolean>
}