package com.squad.update.core.data.util.impl

import com.squad.update.core.data.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ConnectivityManagerNetworkMonitor @Inject constructor() : NetworkMonitor {

    private val connectivityFlow = MutableStateFlow( true )

    override val isOnline = connectivityFlow
}