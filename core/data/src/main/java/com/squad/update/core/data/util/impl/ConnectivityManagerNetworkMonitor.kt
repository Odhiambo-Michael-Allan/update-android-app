package com.squad.update.core.data.util.impl

import android.content.Context
import android.health.connect.ReadRecordsRequestUsingIds.Builder
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.core.content.getSystemService
import com.squad.update.core.common.network.Dispatcher
import com.squad.update.core.common.network.UpdateDispatchers
import com.squad.update.core.data.util.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ConnectivityManagerNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher( UpdateDispatchers.IO ) private val ioDispatcher: CoroutineDispatcher,
) : NetworkMonitor {

    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        if ( connectivityManager == null ) {
            channel.trySend( false )
            channel.close()
            return@callbackFlow
        }
        /**
         * The callback's methods are invoked on changes to *any* network matching the
         * [NetworkRequest], not just the active network. So we can simply track
         * presence ( or absence ) of such [Network].
         */
        val callback = object : NetworkCallback() {
            private val networks = mutableSetOf<Network>()

            override fun onAvailable( network: Network ) {
                networks += network
                channel.trySend( true )
            }

            override fun onLost( network: Network ) {
                networks -= network
                channel.trySend( networks.isNotEmpty() )
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability( NetworkCapabilities.NET_CAPABILITY_INTERNET )
            .build()
        connectivityManager.registerNetworkCallback( request, callback )

        /**
         * Sends the latest connectivity status to the underlying channel.
         */
        channel.trySend( connectivityManager.isCurrentlyConnected() )

        awaitClose {
            connectivityManager.unregisterNetworkCallback( callback )
        }
    }.flowOn( ioDispatcher )
        .conflate()

    @Suppress( "DEPRECATION" )
    private fun ConnectivityManager.isCurrentlyConnected() = when {
        Build.VERSION.SDK_INT >= VERSION_CODES.M ->
            activeNetwork
                ?.let( ::getNetworkCapabilities )
                ?.hasCapability( NetworkCapabilities.NET_CAPABILITY_INTERNET )
        else -> activeNetworkInfo?.isConnected
    } ?: false
}